package io.mustelidae.otter.lutrogale.config.filter

import io.mustelidae.otter.lutrogale.utils.toJson
import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.util.StreamUtils
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.UUID

class RequestResponseLogFilter : OncePerRequestFilter() {
    private val defaultCharset = Charset.forName("utf-8")

    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val isAsync = isAsyncDispatch(request)
        val startTime = System.currentTimeMillis()
        val transactionId = UUID.randomUUID().toString()
        val messageMap = mutableMapOf<String, Any?>()

        if (isSkipUri(request) || isAsync) {
            filterChain.doFilter(request, response)
        } else {
            val multiReadRequest = request as? MultiReadHttpServletRequest ?: MultiReadHttpServletRequest(request)
            val wrappedResponse = response as? ContentCachingResponseWrapper ?: ContentCachingResponseWrapper(response)

            try {
                run {
                    appendTransactionId(messageMap, transactionId)
                    appendHttpMethod(messageMap, request)
                    appendUrl(messageMap, "req", request)
                    appendReqHeader(messageMap, request)

                    if (log.isDebugEnabled) {
                        appendRequestBody(messageMap, multiReadRequest)
                    }
                }
                log.info(messageMap.toJson())
                messageMap.clear()

                filterChain.doFilter(multiReadRequest, wrappedResponse)
            } finally {
                run {
                    appendTransactionId(messageMap, transactionId)
                    appendHttpMethod(messageMap, request)
                    appendUrl(messageMap, "res", request)
                    appendStatus(messageMap, wrappedResponse)
                    appendLatency(messageMap, startTime)

                    if (log.isDebugEnabled) {
                        appendResponseBody(messageMap, wrappedResponse)
                    }

                    log.info(messageMap.toJson())
                    messageMap.clear()
                }

                wrappedResponse.copyBodyToResponse()
            }
        }
    }

    private fun appendUrl(messageMap: MutableMap<String, Any?>, prefix: String, request: HttpServletRequest) {
        messageMap.apply {
            this["transfer"] = prefix
            this["uri"] = request.requestURI
        }
        request.queryString?.let {
            messageMap["query"] = it
        }
    }

    private fun appendReqHeader(messageMap: MutableMap<String, Any?>, request: HttpServletRequest) {
        val headers = ServletServerHttpRequest(request).headers

        messageMap["headers"] = headers
    }

    private fun appendHttpMethod(messageMap: MutableMap<String, Any?>, request: HttpServletRequest) {
        messageMap.apply {
            this["method"] = request.method
            this["contentType"] = request.contentType
            this["encoding"] = request.characterEncoding
        }
    }

    private fun appendTransactionId(messageMap: MutableMap<String, Any?>, transactionId: String) {
        messageMap["txId"] = transactionId
    }

    private fun appendRequestBody(messageMap: MutableMap<String, Any?>, request: MultiReadHttpServletRequest) {
        val requestBody = try {
            StreamUtils.copyToString(request.inputStream, defaultCharset).trimIndent()
        } catch (e: IOException) {
            """{ "error": "Failed to read request body.", "cause": "${e.message}" }""".trimIndent()
        }

        messageMap["requestBody"] = PrivacyLogFilter.masking(requestBody)
    }

    private fun appendStatus(messageMap: MutableMap<String, Any?>, wrappedResponse: ContentCachingResponseWrapper) {
        messageMap["status"] = wrappedResponse.status
    }

    private fun appendLatency(messageMap: MutableMap<String, Any?>, startTime: Long) {
        messageMap["latency"] = "${System.currentTimeMillis() - startTime}ms"
    }

    private fun appendResponseBody(messageMap: MutableMap<String, Any?>, wrappedResponse: ContentCachingResponseWrapper) {
        // Web Page는 로그에서 제외한다.
        if (wrappedResponse.contentType != MediaType.APPLICATION_JSON_VALUE) {
            return
        }

        val responseBody = wrappedResponse.contentAsByteArray.toString(defaultCharset)
        messageMap["responseBody"] = PrivacyLogFilter.masking(responseBody)
    }

    private fun isSkipUri(request: HttpServletRequest): Boolean {
        val uri = request.requestURI

        return (
            uri.startsWith("/health") ||
                uri.startsWith("/favicon.ico") ||
                uri.startsWith("/h2-console") ||
                uri.startsWith("/actuator") ||
                uri.startsWith("/webjars") ||
                uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/v2/api-docs")
            )
    }
}

internal class MultiReadHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private var cachedBytes: ByteArray? = null

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        if (cachedBytes == null) {
            val out = ByteArrayOutputStream()

            StreamUtils.copy(super.getInputStream(), out)
            cachedBytes = out.toByteArray()
        }

        return CachedServletInputStream()
    }

    @Throws(IOException::class)
    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream))
    }

    inner class CachedServletInputStream : ServletInputStream() {

        override fun isReady(): Boolean = true
        override fun isFinished(): Boolean = true
        override fun setReadListener(listener: ReadListener) {
            // NOSONAR
        }

        private val input: ByteArrayInputStream = ByteArrayInputStream(cachedBytes)

        @Throws(IOException::class)
        override fun read(): Int {
            return input.read()
        }
    }
}

private object PrivacyLogFilter {
    private val logger = LoggerFactory.getLogger(PrivacyLogFilter::class.java)

    private const val STRING_PATTERN = "\"%s\"\\s*:\\s*\"([^\"]+)\",?"
    private const val NUMBER_PATTERN = "\"%s\"\\s*:\\s*([0-9]+)"
    private val privacyTargetPatterns = listOf(
        STRING_PATTERN.format("latitude").toRegex(),
        STRING_PATTERN.format("address").toRegex(),
        STRING_PATTERN.format("phone").toRegex(),
        STRING_PATTERN.format("plateNumber").toRegex(),
        NUMBER_PATTERN.format("userId").toRegex(),
        NUMBER_PATTERN.format("latitude").toRegex(),
        NUMBER_PATTERN.format("longitude").toRegex(),
    )

    fun masking(input: String): String {
        return try {
            var replace = input
            for (pattern in privacyTargetPatterns) {
                val matchResults = pattern.findAll(replace)
                for (matchResult in matchResults) {
                    val range = matchResult.groups.last()!!.range
                    val size = (range.last - range.first) + 1
                    replace = replace.replaceRange(range, "*".repeat(size))
                }
            }
            replace
        } catch (e: Exception) {
            logger.error("", e)
            "Privacy Masking Error"
        }
    }
}

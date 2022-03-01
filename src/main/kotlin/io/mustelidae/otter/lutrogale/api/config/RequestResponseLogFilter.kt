package io.mustelidae.otter.lutrogale.api.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
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
import javax.servlet.FilterChain
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse

class RequestResponseLogFilter : OncePerRequestFilter() {

    private val beforeMessagePrefix = "[REQ]"
    private val afterMessagePrefix = "[RES]"
    private val defaultCharset = Charset.forName("utf-8")

    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val isAsync = isAsyncDispatch(request)
        val startTime = System.currentTimeMillis()

        if (isSkipUri(request)) {
            filterChain.doFilter(request, response)
        } else {
            if (isAsync) {
                filterChain.doFilter(request, response)
            } else {
                val multiReadRequest = request as? MultiReadHttpServletRequest ?: MultiReadHttpServletRequest(request)
                val wrappedResponse = response as? ContentCachingResponseWrapper ?: ContentCachingResponseWrapper(response)

                try {
                    beforeRequest(multiReadRequest)
                    filterChain.doFilter(multiReadRequest, wrappedResponse)
                } finally {
                    afterRequest(multiReadRequest, wrappedResponse, startTime)
                    wrappedResponse.copyBodyToResponse()
                }
            }
        }
    }

    private fun beforeRequest(request: HttpServletRequest) {
        val msg = loggingMessage(beforeMessagePrefix, request)
        request.getSession(false)?.let { msg.append(";session=").append(it.id) }
        request.remoteUser?.let { msg.append(";user=").append(it) }
        msg.append(";headers=").append(ServletServerHttpRequest(request).headers)
        readBody(request)?.let { msg.append(";payload=").append(PrivacyLogFilter.masking(it)) }
        log.info(msg.toString())
    }

    private fun afterRequest(request: HttpServletRequest, wrappedResponse: ContentCachingResponseWrapper, startTime: Long) {
        val msg = loggingMessage(afterMessagePrefix, request)

        request.queryString?.let { msg.append('?').append(it) }
        request.remoteAddr?.let { msg.append(";client=").append(it) }
        msg.append(";status=").append(wrappedResponse.status)
        msg.append(";latency=").append(System.currentTimeMillis() - startTime).append("ms")

        if (HttpStatus.valueOf(wrappedResponse.status).is2xxSuccessful) {
            log.info(msg.toString())
        } else {
            wrappedResponse.contentAsByteArray.let { msg.append(";payload=").append(it.toString(defaultCharset)) }
            log.debug(msg.toString())
        }
    }

    private fun loggingMessage(prefix: String, request: HttpServletRequest): StringBuilder {
        val msg = StringBuilder()
        msg.append(prefix)
        msg.append(";uri=").append(request.requestURI)

        request.queryString?.let { msg.append('?').append(it) }
        request.remoteAddr?.let { msg.append(";client=").append(it) }
        return msg
    }

    private fun readBody(request: HttpServletRequest): String? {
        return try {
            if (request.inputStream == null) null else StreamUtils.copyToString(request.inputStream, defaultCharset)
        } catch (e: IOException) {
            "can't read. cause: ${e.message}"
        }
    }

    private fun isSkipUri(request: HttpServletRequest): Boolean {
        val uri = request.requestURI

        return (
            uri.startsWith("/health") ||
                uri.startsWith("/favicon.ico") ||
                uri.startsWith("/h2-console") ||
                uri.startsWith("/actuator/health") ||
                uri.startsWith("/webjars/springfox-swagger-ui") ||
                uri.startsWith("/swagger-ui/") ||
                uri.startsWith("/swagger-ui.html") ||
                uri.startsWith("/swagger-ui/index.html") ||
                uri.startsWith("/v3/api-docs")
            )
    }
}

internal class MultiReadHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private var cachedBytes: ByteArray? = null

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        if (cachedBytes == null) {
            cacheInputStream()
        }

        return CachedServletInputStream()
    }

    @Throws(IOException::class)
    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream))
    }

    @Throws(IOException::class)
    private fun cacheInputStream() {
        val out = ByteArrayOutputStream()

        StreamUtils.copy(super.getInputStream(), out)
        cachedBytes = out.toByteArray()
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

    private const val stringValuePattern = "\"%s\"\\s*:\\s*\"([^\"]+)\",?"
    private const val intValuePattern = "\"%s\"\\s*:\\s*([0-9]+)"
    private val logPatterns = listOf(
        stringValuePattern.format("latitude").toRegex(),
        stringValuePattern.format("address").toRegex(),
        stringValuePattern.format("phone").toRegex(),
        stringValuePattern.format("plateNumber").toRegex(),
        intValuePattern.format("userId").toRegex(),
        intValuePattern.format("latitude").toRegex(),
        intValuePattern.format("longitude").toRegex(),
    )

    fun masking(input: String): String {
        return try {
            var replace = input
            for (pattern in logPatterns) {
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

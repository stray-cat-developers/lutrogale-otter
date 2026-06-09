package io.mustelidae.otter.lutrogale.api.domain.authorization

import com.fasterxml.jackson.databind.ObjectMapper
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources
import io.mustelidae.otter.lutrogale.common.Replies
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.Instant

@Aspect
@Component
class AuthorizationAuditAspect(
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val auditLog = LoggerFactory.getLogger("audit.authorization")

    @Around(
        "execution(* io.mustelidae.otter.lutrogale.api.domain.authorization.api.AuthorizationController.idChecks(..)) || " +
            "execution(* io.mustelidae.otter.lutrogale.api.domain.authorization.api.AuthorizationController.urlCheck(..)) || " +
            "execution(* io.mustelidae.otter.lutrogale.api.domain.authorization.api.AuthorizationController.graphQLCheck(..))",
    )
    fun audit(pjp: ProceedingJoinPoint): Any? {
        val startTime = System.currentTimeMillis()
        val servletRequest = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request

        val rawApiKey = servletRequest?.getHeader("x-system-id") ?: (pjp.args.getOrNull(0) as? String) ?: ""
        val apiKey = maskApiKey(rawApiKey)
        val clientIp = servletRequest?.remoteAddr ?: ""
        val userAgent = servletRequest?.getHeader("User-Agent") ?: ""
        val txId = MDC.get("txId") ?: ""
        val email = extractEmail(pjp.args)
        val checkType = resolveCheckType(pjp.signature.name)

        var caughtException: Throwable? = null
        val result = try {
            pjp.proceed()
        } catch (e: Throwable) {
            caughtException = e
            null
        }

        try {
            val latencyMs = System.currentTimeMillis() - startTime

            val states = if (caughtException == null) {
                (result as? Replies<*>)
                    ?.getContent()
                    ?.filterIsInstance<AccessResources.Reply.AccessState>()
                    ?: emptyList()
            } else {
                emptyList()
            }
            val totalCount = states.size
            val deniedCount = states.count { !it.hasPermission }

            val entry = linkedMapOf<String, Any?>(
                "timestamp" to Instant.now().toString(),
                "txId" to txId,
                "event" to "authorization_check",
                "apiKey" to apiKey,
                "email" to email,
                "checkType" to checkType,
                "clientIp" to clientIp,
                "userAgent" to userAgent,
                "latencyMs" to latencyMs,
                "allowed" to (caughtException == null && deniedCount == 0),
                "deniedCount" to deniedCount,
                "totalCount" to totalCount,
            )
            auditLog.info(objectMapper.writeValueAsString(entry))
        } catch (e: Exception) {
            log.warn("Failed to write authorization audit log", e)
        }

        if (caughtException != null) throw caughtException
        return result
    }

    private fun extractEmail(args: Array<Any>): String {
        return when (val req = args.getOrNull(1)) {
            is AccessResources.Request.IdBase -> req.email
            is AccessResources.Request.UriBase -> req.email
            is AccessResources.Request.GraphQLBase -> req.email
            else -> ""
        }
    }

    private fun resolveCheckType(methodName: String): String = when (methodName) {
        "idChecks" -> "ID"
        "urlCheck" -> "URI"
        "graphQLCheck" -> "GRAPHQL"
        else -> "UNKNOWN"
    }

    private fun maskApiKey(apiKey: String): String {
        if (apiKey.length <= 8) return "****"
        return apiKey.take(8) + "****"
    }
}

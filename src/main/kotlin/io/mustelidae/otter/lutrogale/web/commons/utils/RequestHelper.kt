package io.mustelidae.otter.lutrogale.web.commons.utils

import com.google.common.base.Strings
import io.mustelidae.otter.lutrogale.web.commons.annotations.LoginCheck
import io.mustelidae.otter.lutrogale.web.domain.session.OsoriSessionInfo
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.method.HandlerMethod
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

/**
 * Created by seooseok on 2016. 9. 26..
 * request를 이용한 각종 유틸 제공
 */
object RequestHelper {
    private val apiUriPattern = Pattern.compile("/api")
    private val jsonPattern = Pattern.compile(MediaType.APPLICATION_JSON_VALUE)
    @JvmStatic
    fun isApiRequest(request: HttpServletRequest): Boolean {
        val uri = request.requestURI
        val accept = request.getHeader(HttpHeaders.ACCEPT)
        val contentType = request.getHeader(HttpHeaders.CONTENT_TYPE)
        return (
            apiUriPattern.matcher(uri).find() ||
                !Strings.isNullOrEmpty(accept) && jsonPattern.matcher(accept).find() || !Strings.isNullOrEmpty(
                contentType
            ) && jsonPattern.matcher(contentType).find()
            )
    }

    fun hasResponseBodyAnnotation(handlerMethod: HandlerMethod): Boolean {
        var responseBody = AnnotationUtils.findAnnotation(handlerMethod.method, ResponseBody::class.java)
        if (responseBody == null) responseBody =
            AnnotationUtils.findAnnotation(handlerMethod.beanType, ResponseBody::class.java)
        return responseBody != null
    }

    @JvmStatic
    fun hasLoginCheckAnnotation(handlerMethod: HandlerMethod): Boolean {
        var loginCheck: LoginCheck? =
            AnnotationUtils.findAnnotation<LoginCheck>(handlerMethod.beanType, LoginCheck::class.java)
        if (loginCheck == null || !loginCheck.enable) loginCheck =
            AnnotationUtils.findAnnotation<LoginCheck>(handlerMethod.method, LoginCheck::class.java)
        return !(loginCheck == null || !loginCheck.enable)
    }

    fun addSessionBy(httpSession: HttpSession, osoriSessionInfo: OsoriSessionInfo) {
        httpSession.setAttribute(AttrKey.adminId.name, osoriSessionInfo.adminId)
        httpSession.setAttribute(AttrKey.adminEmail.name, osoriSessionInfo.adminEmail)
        httpSession.setAttribute(AttrKey.adminName.name, osoriSessionInfo.adminName)
    }

    fun addSessionBy(request: HttpServletRequest, osoriSessionInfo: OsoriSessionInfo) {
        addSessionBy(request.getSession(true), osoriSessionInfo)
    }

    @Throws(IllegalArgumentException::class)
    fun getSessionByAdmin(httpSession: HttpSession): OsoriSessionInfo {
        val adminId = httpSession.getAttribute(AttrKey.adminId.name)
        val adminEmail = httpSession.getAttribute(AttrKey.adminEmail.name)
        val adminName = httpSession.getAttribute(AttrKey.adminName.name)
        require(!(adminId == null || adminEmail == null || adminName == null)) { "admin login session is null" }
        return OsoriSessionInfo(
            adminId as Long,
            adminEmail as String,
            adminName as String
        )
    }

    @JvmStatic
    fun getSessionByAdmin(request: HttpServletRequest): OsoriSessionInfo {
        return getSessionByAdmin(request.session)
    }

    private enum class AttrKey {
        adminId, adminEmail, adminName
    }
}

package io.mustelidae.otter.lutrogale.utils

import com.google.common.base.Strings
import io.mustelidae.otter.lutrogale.web.commons.annotations.LoginCheck
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.method.HandlerMethod
import java.util.regex.Pattern

/**
 * Created by seooseok on 2016. 9. 26..
 * request를 이용한 각종 유틸 제공
 */
object RequestHelper {
    private val apiUriPattern = Pattern.compile("/v1")
    private val jsonPattern = Pattern.compile(MediaType.APPLICATION_JSON_VALUE)

    @JvmStatic
    fun isApiRequest(request: HttpServletRequest): Boolean {
        val uri = request.requestURI
        val accept = request.getHeader(HttpHeaders.ACCEPT)
        val contentType = request.getHeader(HttpHeaders.CONTENT_TYPE)
        return (
            apiUriPattern.matcher(uri).find() ||
                !Strings.isNullOrEmpty(accept) && jsonPattern.matcher(accept).find() || !Strings.isNullOrEmpty(
                    contentType,
                ) && jsonPattern.matcher(contentType).find()
            )
    }

    @JvmStatic
    fun hasLoginCheckAnnotation(handlerMethod: HandlerMethod): Boolean {
        var loginCheck: LoginCheck? =
            AnnotationUtils.findAnnotation(handlerMethod.beanType, LoginCheck::class.java)
        if (loginCheck == null || !loginCheck.enable) {
            loginCheck =
                AnnotationUtils.findAnnotation(handlerMethod.method, LoginCheck::class.java)
        }
        return !(loginCheck == null || !loginCheck.enable)
    }
}

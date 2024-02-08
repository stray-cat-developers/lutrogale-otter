package io.mustelidae.otter.lutrogale.config

import io.mustelidae.otter.lutrogale.web.commons.utils.RequestHelper
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.lang.Exception
import java.lang.IllegalArgumentException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.Throws

/**
 * Created by seooseok on 2016. 8. 19..
 * 로그인 인터프리터
 */
@Configuration
class AdminLoginHandlerInterceptor : HandlerInterceptor {
    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        // Handler가 HandlerMethod가 아닌 경우는 Login을 체크 할 수 없기 때문에 true를 반환한다.
        if (handler !is HandlerMethod) return true
        if (!RequestHelper.hasLoginCheckAnnotation(handler)) return true
        try {
            RequestHelper.getSessionByAdmin(request)
        } catch (e: IllegalArgumentException) {
            response.sendRedirect("/login.html")
            return false
        }
        return true
    }

    @Throws(Exception::class)
    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
    }

    @Throws(Exception::class)
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
    }
}

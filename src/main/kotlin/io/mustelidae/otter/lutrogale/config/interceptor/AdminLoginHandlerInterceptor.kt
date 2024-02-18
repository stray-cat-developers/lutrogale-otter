package io.mustelidae.otter.lutrogale.config.interceptor

import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.HumanException
import io.mustelidae.otter.lutrogale.utils.RequestHelper
import io.mustelidae.otter.lutrogale.web.AdminSession
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

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
            AdminSession(request.session).infoOrThrow()
        } catch (e: IllegalArgumentException) {
            if (RequestHelper.isApiRequest(request)) {
                throw HumanException(DefaultError(ErrorCode.HA00, "로그인을 해야만 사용할 수 있는 API 입니다."))
            }

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
        modelAndView: ModelAndView?,
    ) {
    }

    @Throws(Exception::class)
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
    }
}

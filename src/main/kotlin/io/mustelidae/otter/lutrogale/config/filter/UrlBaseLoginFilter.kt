package io.mustelidae.otter.lutrogale.config.filter

import io.mustelidae.otter.lutrogale.web.AdminSession
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.IOException

/**
 * Created by seooseok on 2016. 10. 31..
 */
class UrlBaseLoginFilter : Filter {
    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val servletRequest = request as HttpServletRequest
        if (servletRequest.requestURI.startsWith("/view")) {
            try {
                AdminSession(request.session).infoOrThrow()
            } catch (e: IllegalArgumentException) {
                (response as HttpServletResponse).sendRedirect("/login.html")
            }
        }
        chain.doFilter(request, response)
    }

    override fun destroy() {}
}

package io.mustelidae.otter.lutrogale.web.commons.filter

import io.mustelidae.otter.lutrogale.web.commons.utils.RequestHelper
import java.io.IOException
import java.lang.IllegalArgumentException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.Throws

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
                RequestHelper.getSessionByAdmin(servletRequest)
            } catch (e: IllegalArgumentException) {
                (response as HttpServletResponse).sendRedirect("/login.html")
            }
        }
        chain.doFilter(request, response)
    }

    override fun destroy() {}
}

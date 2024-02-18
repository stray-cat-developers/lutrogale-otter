package io.mustelidae.otter.lutrogale.config.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import java.io.IOException
import kotlin.Throws

/**
 * XSS 방지 필터
 */
class CrossScriptingFilter : Filter {
    private var filterConfig: FilterConfig? = null

    /**
     * 필터 초기화
     *
     * @param filterConfig filterConfig
     * @throws ServletException
     */
    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
        this.filterConfig = filterConfig
    }

    /**
     * 필터 체이닝
     *
     * @param request  ServletRequest
     * @param response ServletResponse
     * @param chain    FilterChain
     * @throws IOException
     * @throws ServletException
     */
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        chain.doFilter(RequestWrapper(request as HttpServletRequest), response)
    }

    /**
     * 필터 완료 처리
     */
    override fun destroy() {
        filterConfig = null
    }
}

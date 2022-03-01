package io.mustelidae.otter.lutrogale.web.commons.filter

import com.google.common.base.Strings
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

/**
 * Request 파라미터 관련 처리
 */
@Suppress("FoldInitializerAndIfToElvis")
internal class RequestWrapper(servletRequest: HttpServletRequest?) : HttpServletRequestWrapper(servletRequest) {

    override fun getParameterValues(parameter: String): Array<String>? {
        val values = super.getParameterValues(parameter)

        if (values == null) {
            return null
        }
        if ("pw" == parameter)
            return values

        return values.map { cleanXSS(it) }
            .toTypedArray()
    }

    override fun getParameter(parameter: String): String? {
        val value = super.getParameter(parameter)
        if (value == null)
            return null

        // password는 cleanXss를 처리하지 않는다.
        return if ("pw" == parameter)
            value
        else cleanXSS(value)
    }

    /**
     * XSS 코드를 싹 날려버림 (web에서는 escape 처리 안함)
     *
     * @param param request 파라미터
     * @return XSS 코드 날린 param
     */
    private fun cleanXSS(param: String): String {
        return param.replace(XSS_FORMAT.toRegex(), "")
    }


    companion object {
        private const val XSS_FORMAT =
            "((<|<[/])script>)|((<|<[/])javascript>)|((<|<[/])vbscript>)|((<|<[/])alert>)|((<|<[/])confirm>)"
    }
}

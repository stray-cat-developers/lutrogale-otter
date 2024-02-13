package io.mustelidae.otter.lutrogale.config

import io.mustelidae.otter.lutrogale.utils.RequestHelper
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.ModelAndView
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Created by seooseok on 2016. 9. 19..
 * View Error 처리
 */
@Configuration
class OsoriErrorViewResolver : ErrorViewResolver {
    override fun resolveErrorView(request: HttpServletRequest, status: HttpStatus, model: Map<String?, Any?>?): ModelAndView {
        val mav = ModelAndView("error")
        if (RequestHelper.isApiRequest(request)) mav.addObject("format", "json")
        mav.addObject("code", status.value())
        mav.addObject("message", status.reasonPhrase)
        mav.addObject("timestamp", LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
        return mav
    }
}

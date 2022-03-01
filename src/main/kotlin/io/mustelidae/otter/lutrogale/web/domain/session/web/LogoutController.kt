package io.mustelidae.otter.lutrogale.web.domain.session.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpSession

/**
 * Created by HanJaehyun on 2016. 9. 22..
 */
@Controller
class LogoutController(
    private val httpSession: HttpSession
) {

    @GetMapping("/logout")
    fun logout(): String {
        httpSession.invalidate()
        return "redirect:/login.html"
    }
}

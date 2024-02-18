package io.mustelidae.otter.lutrogale.web.domain.session.web

import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LogoutController(
    private val httpSession: HttpSession,
) {

    @GetMapping("/logout")
    fun logout(): String {
        httpSession.invalidate()
        return "redirect:/login.html"
    }
}

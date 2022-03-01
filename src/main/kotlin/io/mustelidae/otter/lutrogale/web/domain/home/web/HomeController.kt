package io.mustelidae.otter.lutrogale.web.domain.home.web

import io.mustelidae.smoothcoatedotter.web.commons.annotations.LoginCheck
import io.mustelidae.smoothcoatedotter.web.domain.home.DashBoard
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

/**
 * Created by seooseok on 2016. 9. 23..
 */
@LoginCheck
@Controller
class HomeController(
    private val dashBoard: DashBoard
) {
    @GetMapping("/dashboard")
    fun dashboard(model: Model): String {
        val countByLiveUser: Int = dashBoard.countByLiveTotalUsers()
        val countByWaitUser: Int = dashBoard.countByLiveWaitUsers()
        model.addAttribute("countByLiveUser", countByLiveUser)
        model.addAttribute("countByWaitUser", countByWaitUser)
        return "dashboard"
    }
}

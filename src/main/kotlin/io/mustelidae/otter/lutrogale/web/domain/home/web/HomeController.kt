package io.mustelidae.otter.lutrogale.web.domain.home.web

import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
import io.mustelidae.otter.lutrogale.web.domain.home.DashBoard
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "대시보드")
@LoginCheck
@Controller
class HomeController(
    private val dashBoard: DashBoard,
) {
    @Operation(summary = "대시보드 정보 조회")
    @GetMapping("/dashboard")
    fun dashboard(model: Model): String {
        val countByLiveUser: Int = dashBoard.countByLiveTotalUsers()
        val countByWaitUser: Int = dashBoard.countByLiveWaitUsers()
        model.addAttribute("countByLiveUser", countByLiveUser)
        model.addAttribute("countByWaitUser", countByWaitUser)
        return "dashboard"
    }
}

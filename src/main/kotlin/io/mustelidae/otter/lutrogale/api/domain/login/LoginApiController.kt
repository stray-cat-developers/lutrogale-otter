package io.mustelidae.otter.lutrogale.api.domain.login

import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.web.AdminSession
import io.mustelidae.otter.lutrogale.web.domain.admin.Admin
import io.mustelidae.otter.lutrogale.web.domain.session.SessionInfo
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "로그인 체크", description = "이메일 사용자의 접근 권한 여부를 체크합니다.")
@RestController
class LoginApiController(
    private val httpSession: HttpSession,
    private val adminLoginInteraction: AdminLoginInteraction,
) {

    @Operation(hidden = true)
    @PostMapping("/v1/check-login")
    fun checkLogin(@RequestBody request: LoginResources.Request, httpServletRequest: HttpServletRequest): Reply<String> {
        val admin: Admin = adminLoginInteraction.loginCheck(request.email, request.password)
        val sessionInfo = SessionInfo.of(admin)
        AdminSession(httpSession).add(sessionInfo)
        return admin.email.toReply()
    }
}

package io.mustelidae.otter.lutrogale.web.domain.admin.api

import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.config.PermissionException
import io.mustelidae.otter.lutrogale.web.AdminSession
import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminFinder
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminInteraction
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminRole
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "어드민 관리")
@LoginCheck
@RestController
@RequestMapping("/v1/maintenance/management/admin")
class AdminManagementController(
    private val adminInteraction: AdminInteraction,
    private val adminFinder: AdminFinder,
    private val httpSession: HttpSession,
) {
    @Operation(summary = "어드민 만료 (SUPER 전용)")
    @PostMapping("/{adminId}/expire")
    @ResponseStatus(HttpStatus.CREATED)
    fun expire(@PathVariable adminId: Long): Reply<Unit> {
        val sessionInfo = AdminSession(httpSession).infoOrThrow()
        val caller = adminFinder.findBy(sessionInfo.adminId)
        if (caller.role != AdminRole.SUPER) {
            throw PermissionException("어드민 만료는 SUPER 권한만 가능합니다.")
        }
        adminInteraction.expireBy(adminId, sessionInfo.adminId)
        return Unit.toReply()
    }

    @Operation(summary = "비밀번호 변경 (SUPER: 모든 어드민, REGULAR: 본인만)")
    @PutMapping("/{adminId}/pw")
    fun changePassword(
        @PathVariable adminId: Long,
        @RequestBody request: AdminResources.Request.PasswordChange,
    ): Reply<Unit> {
        val sessionInfo = AdminSession(httpSession).infoOrThrow()
        val caller = adminFinder.findBy(sessionInfo.adminId)
        adminInteraction.changePasswordBy(adminId, sessionInfo.adminId, caller.role, request.pw)
        return Unit.toReply()
    }
}

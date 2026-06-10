package io.mustelidae.otter.lutrogale.web.domain.admin.api

import io.mustelidae.otter.lutrogale.common.Replies
import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReplies
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "어드민 관리")
@LoginCheck
@RestController
@RequestMapping("/v1/maintenance/management/admins")
class AdminsController(
    private val adminInteraction: AdminInteraction,
    private val adminFinder: AdminFinder,
    private val httpSession: HttpSession,
) {
    @Operation(summary = "어드민 목록 조회 (활성 어드민만)")
    @GetMapping
    fun findAll(): Replies<AdminResources.AdminRow> {
        val allAdmins = adminFinder.findAllActive()
        val rootAdmins = allAdmins.filter { it.parentAdmin == null }
        return rootAdmins.map { AdminResources.AdminRow.from(it) }.toReplies()
    }

    @Operation(summary = "어드민 생성 (SUPER 전용)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody request: AdminResources.Request.Create,
    ): Reply<Long> {
        val sessionInfo = AdminSession(httpSession).infoOrThrow()
        val caller = adminFinder.findBy(sessionInfo.adminId)
        if (caller.role != AdminRole.SUPER) {
            throw PermissionException("어드민 생성은 SUPER 권한만 가능합니다.")
        }
        val id =
            adminInteraction.registerBy(
                request.email,
                request.pw,
                request.name,
                request.description,
                null,
                request.role,
                request.parentAdminId,
            )
        return id.toReply()
    }
}

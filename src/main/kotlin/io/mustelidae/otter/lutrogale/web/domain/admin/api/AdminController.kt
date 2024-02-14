package io.mustelidae.otter.lutrogale.web.domain.admin.api

import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.web.AdminSession
import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminFinder
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminInteraction
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpSession
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "어드민")
@LoginCheck
@RestController
@RequestMapping("/v1/maintenance/management/admin")
class AdminController(
    private val adminInteraction: AdminInteraction,
    private val adminFinder: AdminFinder,
    private val httpSession: HttpSession,
) {
    @Operation(summary = "어드민 정보 조회")
    @GetMapping
    fun findOne(): Reply<AdminResources.Reply> {
        val sessionInfo = AdminSession(httpSession).infoOrThrow()
        val admin = adminFinder.findBy(sessionInfo.adminId)
        return AdminResources.Reply.from(admin).toReply()
    }

    @Operation(summary = "어드민 정보 수정", description = "admin의 경우 별도 ID를 받지 않는데 이는 profile 수정의 대상은 무조건 로그인 된 대상만 처리 할 수 있도록 하기 위함이다.")
    @PutMapping
    fun modifyInfo(
        @RequestBody modify: AdminResources.Modify,
    ): Reply<Unit> {
        val sessionInfo = AdminSession(httpSession).infoOrThrow()
        adminInteraction.modifyBy(sessionInfo.adminId, modify.imageUrl, modify.description, modify.pw!!)
        return Unit.toReply()
    }
}

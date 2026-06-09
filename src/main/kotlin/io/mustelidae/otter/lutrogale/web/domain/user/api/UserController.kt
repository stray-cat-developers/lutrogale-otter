package io.mustelidae.otter.lutrogale.web.domain.user.api

import io.mustelidae.otter.lutrogale.common.Replies
import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReplies
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.config.PermissionException
import io.mustelidae.otter.lutrogale.web.AdminSession
import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminFinder
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminRole
import io.mustelidae.otter.lutrogale.web.domain.project.api.ProjectResources
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import io.mustelidae.otter.lutrogale.web.domain.user.UserInteraction
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpSession
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "사용자")
@LoginCheck
@RestController
@RequestMapping("/v1/maintenance/management/user")
class UserController(
    private val userInteraction: UserInteraction,
    private val userFinder: UserFinder,
    private val adminFinder: AdminFinder,
    private val httpSession: HttpSession,
) {

    @Operation(summary = "사용자 추가")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody request: UserResources.Request,
    ): Reply<Long> {
        val user = userInteraction.createBy(request.email, request.name, request.isPrivacy, request.department)
        return user.id!!.toReply()
    }

    @Operation(summary = "사용자 대량 등록 (SUPER 전용)")
    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    fun batchCreate(
        @RequestBody @Valid
        request: UserResources.BatchRegister.Request,
    ): Replies<UserResources.BatchRegister.Result> {
        val sessionInfo = AdminSession(httpSession).infoOrThrow()
        val caller = adminFinder.findBy(sessionInfo.adminId)
        if (caller.role != AdminRole.SUPER) {
            throw PermissionException("사용자 대량 등록은 SUPER 권한만 가능합니다.")
        }
        return userInteraction.bulkCreateBy(
            request.emails,
            request.projectId,
            request.authorityDefinitionId,
            request.initialStatus,
        ).toReplies()
    }

    @Operation(summary = "사용자 조회")
    @GetMapping("/{userId}")
    fun findOne(@PathVariable userId: Long): Reply<UserResources.Reply.Detail> {
        return userFinder.getUserDetail(userId)
            .toReply()
    }

    @Operation(summary = "사용자 수정")
    @PutMapping("/{userId}")
    fun modifyInfo(
        @PathVariable userId: Long,
        @RequestBody modify: UserResources.Modify.Info,
    ): Reply<Unit> {
        userInteraction.modifyBy(userId, modify.name, modify.department, modify.isPrivacy)
        return Unit.toReply()
    }

    @Operation(summary = "사용자가 할당된 프로젝트 리스트 조회")
    @GetMapping("/{userId}/projects")
    fun findUsersProjects(@PathVariable userId: Long): Replies<ProjectResources.Reply> {
        val reply = userFinder.getUserDetail(userId)
        return reply.projects!!
            .toReplies()
    }
}

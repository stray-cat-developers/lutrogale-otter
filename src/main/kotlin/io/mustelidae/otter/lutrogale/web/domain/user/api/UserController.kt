package io.mustelidae.otter.lutrogale.web.domain.user.api

import io.mustelidae.otter.lutrogale.common.Replies
import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReplies
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.web.common.ApiRes
import io.mustelidae.otter.lutrogale.web.common.ApiRes.Companion.success
import io.mustelidae.otter.lutrogale.web.domain.project.api.ProjectResources
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import io.mustelidae.otter.lutrogale.web.domain.user.UserInteraction
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "사용자")
@RestController
@RequestMapping("/v1/maintenance/management/user")
class UserController(
    private val userInteraction: UserInteraction,
    private val userFinder: UserFinder,
) {

    @Operation(summary = "사용자 추가")
    @PostMapping
    fun create(
        @RequestBody request: UserResources.Request,
    ): Reply<Long> {
        val user = userInteraction.createBy(request.email, request.name, request.isPrivacy, request.department)
        return user.id!!.toReply()
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
    ): ApiRes<*> {
        userInteraction.modifyBy(userId, modify.name, modify.department, modify.isPrivacy)
        return success()
    }

    @Operation(summary = "사용자가 할당된 프로젝트 리스트 조회")
    @GetMapping("/{userId}/projects")
    fun findUsersProjects(@PathVariable userId: Long): Replies<ProjectResources.Reply> {
        val reply = userFinder.getUserDetail(userId)
        return reply.projects!!
            .toReplies()
    }
}

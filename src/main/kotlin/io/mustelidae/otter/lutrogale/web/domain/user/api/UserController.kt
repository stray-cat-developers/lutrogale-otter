package io.mustelidae.otter.lutrogale.web.domain.user.api

import io.mustelidae.otter.lutrogale.api.common.Replies
import io.mustelidae.otter.lutrogale.api.common.Reply
import io.mustelidae.otter.lutrogale.api.common.toReplies
import io.mustelidae.otter.lutrogale.api.common.toReply
import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.commons.ApiRes.Companion.success
import io.mustelidae.otter.lutrogale.web.domain.grant.UserGrantInteraction
import io.mustelidae.otter.lutrogale.web.domain.project.api.ProjectResources
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import io.mustelidae.otter.lutrogale.web.domain.user.UserManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by seooseok on 2016. 10. 18..
 */
@RestController
@RequestMapping("/v1/maintenance/management/user")
class UserController(
    private val userGrantInteraction: UserGrantInteraction,
    private val userManager: UserManager,
    private val userFinder: UserFinder
) {

    @PostMapping
    fun create(
        @RequestBody request: UserResources.Request
    ): Reply<Long> {
        val user = userManager.createBy(request.email, request.name, request.isPrivacy, request.department)
        return user.id!!.toReply()
    }

    @GetMapping("/{userId}")
    fun findOne(@PathVariable userId: Long): Reply<UserResources.Reply.Detail> {
        return userFinder.getUserDetail(userId)
            .toReply()
    }

    @PutMapping("/{userId}")
    fun modifyInfo(
        @PathVariable userId: Long,
        @RequestBody modify: UserResources.Modify.Info
    ): ApiRes<*> {
        userManager.modifyBy(userId, modify.name, modify.department, modify.isPrivacy)
        return success()
    }

    @GetMapping("/{userId}/projects")
    fun findUsersProjects(@PathVariable userId: Long): Replies<ProjectResources.Reply> {
        val reply = userFinder.getUserDetail(userId)
        return reply.projects!!
            .toReplies()
    }
}

package io.mustelidae.otter.lutrogale.web.domain.user.api

import io.mustelidae.otter.lutrogale.api.common.Replies
import io.mustelidae.otter.lutrogale.api.common.toReplies
import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.commons.ApiRes.Companion.success
import io.mustelidae.otter.lutrogale.web.domain.user.User
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import io.mustelidae.otter.lutrogale.web.domain.user.UserInteraction
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by seooseok on 2016. 10. 6..
 */
@RestController
@RequestMapping("/v1/maintenance/management/users")
class UsersController(
    private val userInteraction: UserInteraction,
    private val userFinder: UserFinder
) {

    @GetMapping
    fun findAll(@RequestParam(required = false) status: String?): Replies<UserResources.Reply.Simple> {
        var users = userFinder.findByLive()
        status?.let {
            users = users.filter { user -> user.status == User.Status.valueOf(it) }
        }

        return users.map { UserResources.Reply.Simple.from(it) }.toReplies()
    }

    @PutMapping("/{userIds}")
    fun modifyInfo(
        @PathVariable userIds: List<Long>,
        @RequestBody modify: UserResources.Modify.UserState
    ): ApiRes<*> {
        userInteraction.modifyBy(userIds, modify.status)
        return success()
    }

    @DeleteMapping("/{userIds}")
    fun expireStatus(@PathVariable userIds: List<Long>): ApiRes<*> {
        userInteraction.expireBy(userIds)
        return success()
    }
}

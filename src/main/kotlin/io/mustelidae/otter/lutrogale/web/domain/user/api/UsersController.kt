package io.mustelidae.otter.lutrogale.web.domain.user.api

import io.mustelidae.otter.lutrogale.api.common.Replies
import io.mustelidae.otter.lutrogale.api.common.toReplies
import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.commons.ApiRes.Companion.success
import io.mustelidae.otter.lutrogale.web.commons.toApiRes
import io.mustelidae.otter.lutrogale.web.domain.user.User
import io.mustelidae.otter.lutrogale.web.domain.user.UserManager
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by seooseok on 2016. 10. 6..
 */
@RestController
@RequestMapping("/management/users")
class UsersController(
    private val userManager: UserManager
) {

    @GetMapping
    fun findAll(@RequestParam(required = false) status: String?): Replies<UserResource> {
        var users = userManager.findByLive()
        status?.let {
            users = users.filter { user -> user.status == User.Status.valueOf(it) }
        }

        return users.map { UserResource.of(it) }.toReplies()
    }

    @PutMapping("/{userIdGroup}")
    fun modifyInfo(
        @PathVariable userIdGroup: List<Long>,
        @RequestParam(required = false) department: String?,
        @RequestParam(required = false) isPrivacy: Boolean,
        @RequestParam(required = false) status: String?
    ): ApiRes<*> {

        userManager.modifyBy(userIdGroup, department, isPrivacy)

        if (status != null)
            userManager.modifyBy(userIdGroup, User.Status.valueOf(status))

        return success()
    }

    @DeleteMapping("/{userIdGroup}")
    fun expireStatus(@PathVariable userIdGroup: List<Long>): ApiRes<*> {
        userManager.expireBy(userIdGroup)
        return success()
    }
}

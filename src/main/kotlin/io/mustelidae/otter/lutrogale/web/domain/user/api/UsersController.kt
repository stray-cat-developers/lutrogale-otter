package io.mustelidae.otter.lutrogale.web.domain.user.api

import io.mustelidae.otter.lutrogale.common.Replies
import io.mustelidae.otter.lutrogale.common.toReplies
import io.mustelidae.otter.lutrogale.web.common.ApiRes
import io.mustelidae.otter.lutrogale.web.common.ApiRes.Companion.success
import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
import io.mustelidae.otter.lutrogale.web.domain.user.User
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import io.mustelidae.otter.lutrogale.web.domain.user.UserInteraction
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "사용자")
@LoginCheck
@RestController
@RequestMapping("/v1/maintenance/management/users")
class UsersController(
    private val userInteraction: UserInteraction,
    private val userFinder: UserFinder,
) {

    @Operation(summary = "사용자 리스트 조회")
    @GetMapping
    fun findAll(@RequestParam(required = false) status: String?): Replies<UserResources.Reply.Simple> {
        var users = userFinder.findByLive()
        status?.let {
            users = users.filter { user -> user.status == User.Status.valueOf(it) }
        }

        return users.map { UserResources.Reply.Simple.from(it) }.toReplies()
    }

    @Operation(summary = "사용자 정보 일괄 수정")
    @PutMapping("/{userIds}")
    fun modifyInfo(
        @PathVariable userIds: List<Long>,
        @RequestBody modify: UserResources.Modify.UserState,
    ): ApiRes<*> {
        userInteraction.modifyBy(userIds, modify.getStatus())
        return success()
    }

    @Operation(summary = "사용자 정보 일괄 만료")
    @DeleteMapping("/{userIds}")
    fun expireStatus(@PathVariable userIds: List<Long>): ApiRes<*> {
        userInteraction.expireBy(userIds)
        return success()
    }
}

package io.mustelidae.otter.lutrogale.web.domain.grant.api

import io.mustelidae.otter.lutrogale.api.common.Reply
import io.mustelidae.otter.lutrogale.api.common.toReply
import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.domain.grant.UserGrantInteraction
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import io.mustelidae.otter.lutrogale.web.domain.user.api.UserResources
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/maintenance/management/user")
class UserGrantController(
    private val userGrantInteraction: UserGrantInteraction,
    private val userFinder: UserFinder
) {

    @GetMapping("/{userId}/grants")
    fun findUsersGrants(@PathVariable userId: Long): Reply<UserResources.Reply.Detail> {
        return userFinder.getUserDetail(userId)
            .toReply()
    }

    @PostMapping("/{userId}/grant/project/{projectId}/authority-bundle/{authIds}")
    fun assignAuthorityGrant(
        @PathVariable userId: Long,
        @PathVariable projectId: Long,
        @PathVariable(value = "authIds") authDefinitionIds: List<Long>
    ): ApiRes<*> {
        userGrantInteraction.addByAuthorityGrant(userId, projectId, authDefinitionIds)
        return ApiRes.success()
    }

    @DeleteMapping("/{userId}/grant/project/{projectId}/authority-bundle/{authIds}")
    fun withdrawAuthorityGrant(
        @PathVariable userId: Long,
        @PathVariable projectId: Long,
        @PathVariable(value = "authIds") authDefinitionIds: List<Long>
    ): ApiRes<*> {
        userGrantInteraction.removeByAuthorityGrant(userId, projectId, authDefinitionIds)
        return ApiRes.success()
    }

    @PostMapping("/{userId}/grant/project/{projectId}/authority-personal/{menuNaviIds}")
    fun assignPersonalGrant(
        @PathVariable userId: Long,
        @PathVariable projectId: Long,
        @PathVariable(value = "menuNaviIds") menuNavigationIds: List<Long>
    ): ApiRes<*> {
        userGrantInteraction.addByPersonalGrant(userId, projectId, menuNavigationIds)
        return ApiRes.success()
    }

    @DeleteMapping("/{userId}/grant/project/{projectId}/authority-personal/{menuNaviIds}")
    fun withdrawPersonalGrant(
        @PathVariable userId: Long,
        @PathVariable projectId: Long,
        @PathVariable(value = "menuNaviIds") menuNavigationIds: List<Long>
    ): ApiRes<*> {
        userGrantInteraction.removeByPersonalGrant(userId, projectId, menuNavigationIds)
        return ApiRes.success()
    }

    @GetMapping("/{userId}/grant/project/{projectId}")
    fun findGrantsForUser(@PathVariable projectId: Long, @PathVariable userId: Long): ApiRes<*> {
        val authorityGrantResources: List<AuthorityGrantResource> = userGrantInteraction.getUserAuthorityGrants(userId, projectId)
        val personalGrantResources: List<PersonalGrantResource> = userGrantInteraction.getUserPersonalGrants(userId, projectId)
        val map: MutableMap<String, Any> = HashMap()
        map["authorityDefinitions"] = authorityGrantResources
        map["menuNavigations"] = personalGrantResources
        return ApiRes<Any?>(map)
    }
}

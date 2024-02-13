package io.mustelidae.otter.lutrogale.web.domain.grant.api

import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.commons.annotations.LoginCheck
import io.mustelidae.otter.lutrogale.web.domain.grant.UserGrantInteraction
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import io.mustelidae.otter.lutrogale.web.domain.user.api.UserResources
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "사용자 권한")
@LoginCheck
@RestController
@RequestMapping("/v1/maintenance/management/user")
class UserGrantController(
    private val userGrantInteraction: UserGrantInteraction,
    private val userFinder: UserFinder,
) {

    @Operation(summary = "할당된 권한 조회")
    @GetMapping("/{userId}/grants")
    fun findUsersGrants(@PathVariable userId: Long): Reply<UserResources.Reply.Detail> {
        return userFinder.getUserDetail(userId)
            .toReply()
    }

    @Operation(summary = "권한 그룹 추가")
    @PostMapping("/{userId}/grant/project/{projectId}/authority-bundle/{authIds}")
    fun assignAuthorityGrant(
        @PathVariable userId: Long,
        @PathVariable projectId: Long,
        @PathVariable(value = "authIds") authDefinitionIds: List<Long>,
    ): ApiRes<*> {
        userGrantInteraction.addByAuthorityGrant(userId, projectId, authDefinitionIds)
        return ApiRes.success()
    }

    @Operation(summary = "권한 그룹 제거")
    @DeleteMapping("/{userId}/grant/project/{projectId}/authority-bundle/{authIds}")
    fun withdrawAuthorityGrant(
        @PathVariable userId: Long,
        @PathVariable projectId: Long,
        @PathVariable(value = "authIds") authDefinitionIds: List<Long>,
    ): ApiRes<*> {
        userGrantInteraction.removeByAuthorityGrant(userId, projectId, authDefinitionIds)
        return ApiRes.success()
    }

    @Operation(summary = "메뉴 권한 추가")
    @PostMapping("/{userId}/grant/project/{projectId}/authority-personal/{menuNaviIds}")
    fun assignPersonalGrant(
        @PathVariable userId: Long,
        @PathVariable projectId: Long,
        @PathVariable(value = "menuNaviIds") menuNavigationIds: List<Long>,
    ): ApiRes<*> {
        userGrantInteraction.addByPersonalGrant(userId, projectId, menuNavigationIds)
        return ApiRes.success()
    }

    @Operation(summary = "메뉴 권한 제거")
    @DeleteMapping("/{userId}/grant/project/{projectId}/authority-personal/{menuNaviIds}")
    fun withdrawPersonalGrant(
        @PathVariable userId: Long,
        @PathVariable projectId: Long,
        @PathVariable(value = "menuNaviIds") menuNavigationIds: List<Long>,
    ): ApiRes<*> {
        userGrantInteraction.removeByPersonalGrant(userId, projectId, menuNavigationIds)
        return ApiRes.success()
    }

    @Operation(summary = "사용자의 모든 권한 조회")
    @GetMapping("/{userId}/grant/project/{projectId}")
    fun findGrantsForUser(@PathVariable projectId: Long, @PathVariable userId: Long): ApiRes<*> {
        val authorityGrantResources = userGrantInteraction.getUserAuthorityGrants(userId, projectId)
        val personalGrantResources = userGrantInteraction.getUserPersonalGrants(userId, projectId)
        val map: MutableMap<String, Any> = HashMap()
        map["authorityDefinitions"] = authorityGrantResources
        map["menuNavigations"] = personalGrantResources
        return ApiRes<Any?>(map)
    }
}

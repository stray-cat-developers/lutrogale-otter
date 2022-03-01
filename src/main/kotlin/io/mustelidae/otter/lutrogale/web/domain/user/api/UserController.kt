package io.mustelidae.otter.lutrogale.web.domain.user.api

import io.mustelidae.smoothcoatedotter.api.common.Replies
import io.mustelidae.smoothcoatedotter.api.common.Reply
import io.mustelidae.smoothcoatedotter.api.common.toReplies
import io.mustelidae.smoothcoatedotter.api.common.toReply
import io.mustelidae.smoothcoatedotter.web.commons.ApiRes
import io.mustelidae.smoothcoatedotter.web.commons.ApiRes.Companion.success
import io.mustelidae.smoothcoatedotter.web.domain.grant.api.AuthorityGrantResource
import io.mustelidae.smoothcoatedotter.web.domain.grant.api.PersonalGrantResource
import io.mustelidae.smoothcoatedotter.web.domain.project.api.ProjectResources
import io.mustelidae.smoothcoatedotter.web.domain.user.UserGrant
import io.mustelidae.smoothcoatedotter.web.domain.user.UserManager
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by seooseok on 2016. 10. 18..
 */
@RestController
@RequestMapping("/management/user")
class UserController(
    private val userGrant: UserGrant,
    private val userManager: UserManager
) {

    @PostMapping
    fun create(
        @RequestParam email: String,
        @RequestParam name: String,
        @RequestParam department: String? = "",
        @RequestParam isPrivacy: Boolean
    ): Reply<Long> {
        val user = userManager.createBy(email, name, department, isPrivacy)
        return user.id!!.toReply()
    }

    @GetMapping("/{userId}")
    fun findOne(@PathVariable userId: Long): Reply<UserResource> {
        return userManager.getUserDetail(userId)
            .toReply()
    }

    @PutMapping("/{userId}")
    fun modifyInfo(
        @PathVariable userId: Long,
        @RequestParam name: String?,
        @RequestParam department: String?,
        @RequestParam isPrivacy: Boolean
    ): ApiRes<*> {
        val user = userManager.findBy(userId)
        name?.let {
            user.name = name
        }
        user.department = department
        user.isPrivacy = isPrivacy
        userManager.saveBy(user)
        return success()
    }

    @GetMapping("/{userId}/projects")
    fun findUsersProjects(@PathVariable userId: Long): Replies<ProjectResources.Reply> {
        val userResource = userManager.getUserDetail(userId)
        return userResource.projects!!
            .toReplies()
    }

    @GetMapping("/{userId}/grants")
    fun findUsersGrants(@PathVariable userId: Long): Reply<UserResource> {
        val userResource = userManager.getUserDetail(userId)
        return userResource.toReply()
    }

    @PostMapping("/{userId}/grant/project/{projectId}/authority-bundle/{authIdGroup}")
    fun assignAuthorityGrant(
        @PathVariable userId: Long,
        @PathVariable projectId: Long,
        @PathVariable(value = "authIdGroup") authDefinitionIdGroup: List<Long>
    ): ApiRes<*> {
        userGrant.addByAuthorityGrant(userId, projectId, authDefinitionIdGroup)
        return success()
    }

    @DeleteMapping("/{userId}/grant/project/{projectId}/authority-bundle/{authIdGroup}")
    fun withdrawAuthorityGrant(
        @PathVariable userId: Long,
        @PathVariable projectId: Long,
        @PathVariable(value = "authIdGroup") authDefinitionIdGroup: List<Long>
    ): ApiRes<*> {
        userGrant.removeByAuthorityGrant(userId, projectId, authDefinitionIdGroup)
        return success()
    }

    @PostMapping("/{userId}/grant/project/{projectId}/authority-personal/{menuNaviIdGroup}")
    fun assignPersonalGrant(
        @PathVariable userId: Long,
        @PathVariable projectId: Long,
        @PathVariable(value = "menuNaviIdGroup") menuNavigationIdGroup: List<Long>
    ): ApiRes<*> {
        userGrant.addByPersonalGrant(userId, projectId, menuNavigationIdGroup)
        return success()
    }

    @DeleteMapping("/{userId}/grant/project/{projectId}/authority-personal/{menuNaviIdGroup}")
    fun withdrawPersonalGrant(
        @PathVariable userId: Long,
        @PathVariable projectId: Long,
        @PathVariable(value = "menuNaviIdGroup") menuNavigationIdGroup: List<Long>
    ): ApiRes<*> {
        userGrant.removeByPersonalGrant(userId, projectId, menuNavigationIdGroup)
        return success()
    }

    @GetMapping("/{userId}/grant/project/{projectId}")
    fun findGrantsForUser(@PathVariable projectId: Long, @PathVariable userId: Long): ApiRes<*> {
        val authorityGrantResources: List<AuthorityGrantResource> = userGrant.getUserAuthorityGrants(userId, projectId)
        val personalGrantResources: List<PersonalGrantResource> = userGrant.getUserPersonalGrants(userId, projectId)
        val map: MutableMap<String, Any> = HashMap()
        map["authorityDefinitions"] = authorityGrantResources
        map["menuNavigations"] = personalGrantResources
        return ApiRes<Any?>(map)
    }
}

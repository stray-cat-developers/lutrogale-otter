package io.mustelidae.otter.lutrogale.web.domain.authority.api

import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.commons.ApiRes.Companion.success
import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityBundleInteraction
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.MenuNavigationResource
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.TreeBranchResource
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by seooseok on 2016. 9. 30..
 */
@RestController
@RequestMapping("/v1/maintenance/project")
class AuthorityController(
    private val authorityBundleInteraction: AuthorityBundleInteraction
) {

    @PostMapping("/{projectId}/authority-bundle")
    fun create(
        @PathVariable projectId: Long,
        @RequestBody request: AuthorityBundleResources.Request.AuthorityBundle
    ): ApiRes<*> {
        val defineId: Long = authorityBundleInteraction.createBundle(projectId, request.groupName, request.naviId)
        return ApiRes<Any?>(defineId)
    }

    @GetMapping("/{projectId}/authority-bundles")
    fun findAll(@PathVariable projectId: Long): ApiRes<*> {
        val authorityDefinitions = authorityBundleInteraction.getBundles(projectId)
        val authorityBundles = authorityDefinitions.map { AuthorityBundleResources.Reply.AuthorityBundle.from(it) }
        return ApiRes<Any?>(authorityBundles)
    }

    @GetMapping("/{projectId}/authority-bundle/{authId}/navigations")
    fun findBundlesNavigations(
        @PathVariable projectId: Long,
        @PathVariable authId: Long
    ): ApiRes<*> {
        val menuNavigations: List<MenuNavigationResource> = authorityBundleInteraction.lookInBundle(authId)
        return ApiRes<Any?>(menuNavigations)
    }

    @GetMapping("/{projectId}/authority-bundle/{authId}/branches")
    fun findBundlesBranches(
        @PathVariable projectId: Long,
        @PathVariable authId: Long
    ): ApiRes<*> {
        val treeBranchResources: List<TreeBranchResource> = authorityBundleInteraction.lookInBundleForTreeFormat(authId)
        return ApiRes<Any?>(treeBranchResources)
    }

    @PutMapping("/{projectId}/authority-bundle/{authId}")
    fun modifyInfo(
        @PathVariable projectId: Long,
        @PathVariable authId: Long,
        @RequestBody modify: AuthorityBundleResources.Modify.Tree
    ): ApiRes<*> {
        authorityBundleInteraction.mappingNavigationAndDefinition(projectId, authId, modify.naviId)
        return success()
    }

    @DeleteMapping("/{projectId}/authority-bundle/{authId}")
    fun expire(
        @PathVariable projectId: Long,
        @PathVariable authId: Long
    ): ApiRes<*> {
        authorityBundleInteraction.expireBy(projectId, authId)
        return success()
    }

    @DeleteMapping("/{projectId}/authority-bundle/{authId}/navigations/{naviIdGroup}")
    fun expireNavigations(
        @PathVariable projectId: Long,
        @PathVariable authId: Long,
        @PathVariable("naviIdGroup") naviIdGroup: List<Long>
    ): ApiRes<*> {
        authorityBundleInteraction.removeMappingNavigationAndDefinition(projectId, authId, naviIdGroup)
        return success()
    }
}

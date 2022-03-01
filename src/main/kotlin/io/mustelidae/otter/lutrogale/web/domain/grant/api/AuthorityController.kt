package io.mustelidae.otter.lutrogale.web.domain.grant.api

import io.mustelidae.smoothcoatedotter.web.commons.ApiRes
import io.mustelidae.smoothcoatedotter.web.commons.ApiRes.Companion.success
import io.mustelidae.smoothcoatedotter.web.domain.grant.AuthorityBundle
import io.mustelidae.smoothcoatedotter.web.domain.navigation.api.MenuNavigationResource
import io.mustelidae.smoothcoatedotter.web.domain.navigation.api.TreeBranchResource
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by seooseok on 2016. 9. 30..
 */
@RestController
@RequestMapping("/project")
class AuthorityController(
    private val authorityBundle: AuthorityBundle
) {

    @PostMapping("/{projectId}/authority-bundle")
    fun create(
        @PathVariable projectId: Long,
        @RequestParam groupName: String?,
        @RequestParam("naviId[]") naviIdGroup: List<Long>
    ): ApiRes<*> {
        val defineId: Long = authorityBundle.createBundle(projectId, groupName, naviIdGroup)
        return ApiRes<Any?>(defineId)
    }

    @GetMapping("/{projectId}/authority-bundles")
    fun findAll(@PathVariable projectId: Long): ApiRes<*> {
        val authorityDefinitions = authorityBundle.getBundles(projectId)
        val authGroup = authorityDefinitions.map { AuthGroup.of(it) }
        return ApiRes<Any?>(authGroup)
    }

    @GetMapping("/{projectId}/authority-bundle/{authId}/navigations")
    fun findBundlesNavigations(
        @PathVariable projectId: Long,
        @PathVariable authId: Long
    ): ApiRes<*> {
        val menuNavigations: List<MenuNavigationResource> = authorityBundle.lookInBundle(authId)
        return ApiRes<Any?>(menuNavigations)
    }

    @GetMapping("/{projectId}/authority-bundle/{authId}/branches")
    fun findBundlesBranches(
        @PathVariable projectId: Long,
        @PathVariable authId: Long
    ): ApiRes<*> {
        val treeBranchResources: List<TreeBranchResource> = authorityBundle.lookInBundleForTreeFormat(authId)
        return ApiRes<Any?>(treeBranchResources)
    }

    @PutMapping("/{projectId}/authority-bundle/{authId}")
    fun modifyInfo(
        @PathVariable projectId: Long,
        @PathVariable authId: Long,
        @RequestParam naviId: Long
    ): ApiRes<*> {
        authorityBundle.modifyBundlesNavigation(projectId, authId, naviId)
        return success()
    }

    @DeleteMapping("/{projectId}/authority-bundle/{authId}")
    fun expire(
        @PathVariable projectId: Long,
        @PathVariable authId: Long
    ): ApiRes<*> {
        authorityBundle.expireBy(projectId, authId)
        return success()
    }

    @DeleteMapping("/{projectId}/authority-bundle/{authId}/navigations/{naviIdGroup}")
    fun expireNavigations(
        @PathVariable projectId: Long,
        @PathVariable authId: Long,
        @PathVariable("naviIdGroup") naviIdGroup: List<Long>
    ): ApiRes<*> {
        authorityBundle.expireAuthorityNavigation(projectId, authId, naviIdGroup)
        return success()
    }
}

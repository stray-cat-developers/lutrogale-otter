package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.commons.ApiRes.Companion.success
import io.mustelidae.otter.lutrogale.web.domain.navigation.NavigationTree
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = ["/project/{projectId}/menu-tree"])
class MenuTreeController(
    private val navigationTree: NavigationTree
) {

    @PostMapping("/branch")
    fun createBranch(
        @PathVariable projectId: Long,
        @RequestBody branchRequest: BranchRequest
    ): ApiRes<*> {
        val id: Long = navigationTree.createBranch(projectId, branchRequest)
        return ApiRes<Any?>(id)
    }

    @PutMapping("/branch/{menuNavigationId}")
    fun moveBranch(
        @PathVariable projectId: Long,
        @PathVariable menuNavigationId: Long,
        @RequestParam(value = "parentTreeId") parentTreeId: String?
    ): ApiRes<*> {
        navigationTree.moveBranch(projectId, menuNavigationId, parentTreeId)
        return success()
    }

    @DeleteMapping("/branch/{menuNavigationId}")
    fun deleteBranch(@PathVariable projectId: Long, @PathVariable menuNavigationId: Long): ApiRes<*> {
        navigationTree.removeBranch(projectId, menuNavigationId)
        return success()
    }

    @GetMapping("/branch/{menuNavigationId}")
    fun findBranch(@PathVariable projectId: Long, @PathVariable menuNavigationId: Long): ApiRes<*> {
        val treeBranchResource: TreeBranchResource = navigationTree.getTreeBranch(projectId, menuNavigationId)
        return ApiRes<Any?>(treeBranchResource)
    }

    @GetMapping("/branches")
    fun getAllBranch(@PathVariable projectId: Long): ApiRes<*> {
        val treeBranchResources: List<TreeBranchResource> = navigationTree.getTreeBranches(projectId)
        return ApiRes<Any?>(treeBranchResources)
    }
}

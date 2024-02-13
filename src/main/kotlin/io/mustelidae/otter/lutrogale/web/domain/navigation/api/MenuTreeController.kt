package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.commons.ApiRes.Companion.success
import io.mustelidae.otter.lutrogale.web.commons.annotations.LoginCheck
import io.mustelidae.otter.lutrogale.web.domain.navigation.NavigationTreeInteraction
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "메뉴 트리")
@LoginCheck
@RestController
@RequestMapping(value = ["/v1/maintenance/project/{projectId}/menu-tree"])
class MenuTreeController(
    private val navigationTreeInteraction: NavigationTreeInteraction,
) {

    @Operation(summary = "메뉴 트리 추가")
    @PostMapping("/branch")
    fun createBranch(
        @PathVariable projectId: Long,
        @RequestBody request: MenuTreeResources.Request.Branch,
    ): ApiRes<*> {
        val id: Long = navigationTreeInteraction.createBranch(projectId, request)
        return ApiRes<Any?>(id)
    }

    @Operation(summary = "메뉴 트리 이동")
    @PutMapping("/branch/{menuNavigationId}")
    fun moveBranch(
        @PathVariable projectId: Long,
        @PathVariable menuNavigationId: Long,
        @RequestParam(value = "parentTreeId") parentTreeId: String?,
    ): ApiRes<*> {
        navigationTreeInteraction.moveBranch(projectId, menuNavigationId, parentTreeId)
        return success()
    }

    @Operation(summary = "메뉴 트리 삭제")
    @DeleteMapping("/branch/{menuNavigationId}")
    fun deleteBranch(@PathVariable projectId: Long, @PathVariable menuNavigationId: Long): ApiRes<*> {
        navigationTreeInteraction.removeBranch(projectId, menuNavigationId)
        return success()
    }

    @Operation(summary = "메뉴 트리 조회")
    @GetMapping("/branch/{menuNavigationId}")
    fun findBranch(@PathVariable projectId: Long, @PathVariable menuNavigationId: Long): ApiRes<*> {
        val treeBranchResource = navigationTreeInteraction.getTreeBranch(projectId, menuNavigationId)
        return ApiRes<Any?>(treeBranchResource)
    }

    @Operation(summary = "메뉴 트리 전체 조회")
    @GetMapping("/branches")
    fun getAllBranch(@PathVariable projectId: Long): ApiRes<*> {
        val treeBranchResources = navigationTreeInteraction.getTreeBranches(projectId)
        return ApiRes<Any?>(treeBranchResources)
    }
}

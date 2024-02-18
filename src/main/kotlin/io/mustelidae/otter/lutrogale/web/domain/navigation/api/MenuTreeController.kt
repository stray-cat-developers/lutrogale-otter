package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import io.mustelidae.otter.lutrogale.common.Replies
import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReplies
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
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
    ): Reply<Long> {
        return navigationTreeInteraction.createBranch(projectId, request).toReply()
    }

    @Operation(summary = "메뉴 트리 이동")
    @PutMapping("/branch/{menuNavigationId}")
    fun moveBranch(
        @PathVariable projectId: Long,
        @PathVariable menuNavigationId: Long,
        @RequestBody parentTreeId: String?,
    ): Reply<Unit> {
        navigationTreeInteraction.moveBranch(projectId, menuNavigationId, parentTreeId)
        return Unit.toReply()
    }

    @Operation(summary = "메뉴 트리 삭제")
    @DeleteMapping("/branch/{menuNavigationId}")
    fun deleteBranch(@PathVariable projectId: Long, @PathVariable menuNavigationId: Long): Reply<Unit> {
        navigationTreeInteraction.removeBranch(projectId, menuNavigationId)
        return Unit.toReply()
    }

    @Operation(summary = "메뉴 트리 조회")
    @GetMapping("/branch/{menuNavigationId}")
    fun findBranch(@PathVariable projectId: Long, @PathVariable menuNavigationId: Long): Reply<MenuTreeResources.Reply.TreeBranch> {
        return navigationTreeInteraction.getTreeBranch(projectId, menuNavigationId)
            .toReply()
    }

    @Operation(summary = "메뉴 트리 전체 조회")
    @GetMapping("/branches")
    fun getAllBranch(@PathVariable projectId: Long): Replies<MenuTreeResources.Reply.TreeBranch> {
        return navigationTreeInteraction.getTreeBranches(projectId)
            .toReplies()
    }
}

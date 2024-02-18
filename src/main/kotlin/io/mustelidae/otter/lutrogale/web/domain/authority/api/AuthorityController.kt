package io.mustelidae.otter.lutrogale.web.domain.authority.api

import io.mustelidae.otter.lutrogale.common.Replies
import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReplies
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityBundleInteraction
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.MenuTreeResources.Reply.TreeBranch
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.NavigationResources.Reply.ReplyOfMenuNavigation
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

@Tag(name = "권한 그룹")
@LoginCheck
@RestController
@RequestMapping("/v1/maintenance/project")
class AuthorityController(
    private val authorityBundleInteraction: AuthorityBundleInteraction,
) {

    @Operation(summary = "권한 그룹 생성")
    @PostMapping("/{projectId}/authority-bundle")
    fun create(
        @PathVariable projectId: Long,
        @RequestBody request: AuthorityBundleResources.Request.AuthorityBundle,
    ): Reply<Long> {
        val defineId: Long = authorityBundleInteraction.createBundle(projectId, request.groupName, request.naviId)
        return defineId.toReply()
    }

    @Operation(summary = "권한 그룹 조회")
    @GetMapping("/{projectId}/authority-bundles")
    fun findAll(@PathVariable projectId: Long): Replies<AuthorityBundleResources.Reply.AuthorityBundle> {
        val authorityDefinitions = authorityBundleInteraction.getBundles(projectId)
        return authorityDefinitions
            .map { AuthorityBundleResources.Reply.AuthorityBundle.from(it) }
            .toReplies()
    }

    @Operation(summary = "권한 그룹의 메뉴 네비게이션 전체 조회")
    @GetMapping("/{projectId}/authority-bundle/{authId}/navigations")
    fun findBundlesNavigations(
        @PathVariable projectId: Long,
        @PathVariable authId: Long,
    ): Replies<ReplyOfMenuNavigation> {
        return authorityBundleInteraction.lookInBundle(authId)
            .toReplies()
    }

    @Operation(summary = "권한 그룹의 메뉴 네비게이션 트리 구조 조회")
    @GetMapping("/{projectId}/authority-bundle/{authId}/branches")
    fun findBundlesBranches(
        @PathVariable projectId: Long,
        @PathVariable authId: Long,
    ): Replies<TreeBranch> {
        val treeBranches = authorityBundleInteraction.lookInBundleForTreeFormat(authId)
        return treeBranches.toReplies()
    }

    @Operation(summary = "권한 그룹 수정")
    @PutMapping("/{projectId}/authority-bundle/{authId}")
    fun modifyInfo(
        @PathVariable projectId: Long,
        @PathVariable authId: Long,
        @RequestBody modify: AuthorityBundleResources.Modify.Tree,
    ): Reply<Unit> {
        authorityBundleInteraction.mappingNavigationAndDefinition(projectId, authId, modify.naviId)
        return Unit.toReply()
    }

    @Operation(summary = "권한 그룹 만료")
    @DeleteMapping("/{projectId}/authority-bundle/{authId}")
    fun expire(
        @PathVariable projectId: Long,
        @PathVariable authId: Long,
    ): Reply<Unit> {
        authorityBundleInteraction.expireBy(projectId, authId)
        return Unit.toReply()
    }

    @Operation(summary = "네비게이션 만료")
    @DeleteMapping("/{projectId}/authority-bundle/{authId}/navigations/{naviIdGroup}")
    fun expireNavigations(
        @PathVariable projectId: Long,
        @PathVariable authId: Long,
        @PathVariable("naviIdGroup") naviIdGroup: List<Long>,
    ): Reply<Unit> {
        authorityBundleInteraction.removeMappingNavigationAndDefinition(projectId, authId, naviIdGroup)
        return Unit.toReply()
    }
}

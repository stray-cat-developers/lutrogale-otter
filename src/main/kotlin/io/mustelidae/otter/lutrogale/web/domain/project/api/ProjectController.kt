package io.mustelidae.otter.lutrogale.web.domain.project.api

import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.commons.annotations.LoginCheck
import io.mustelidae.otter.lutrogale.web.commons.toApiRes
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.NavigationResources.Reply.ReplyOfMenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectInteraction
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import io.mustelidae.otter.lutrogale.web.domain.user.api.UserResources
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "프로젝트")
@LoginCheck
@RestController
@RequestMapping("/v1/maintenance")
class ProjectController(
    private val projectInteraction: ProjectInteraction,
    private val userFinder: UserFinder,
    private val projectFinder: ProjectFinder,
) {

    @Operation(summary = "프로젝트 전체 조회")
    @GetMapping("/projects")
    fun findAll(): ApiRes<*> {
        return projectFinder.findAllByLive()
            .map { ProjectResources.Reply.from(it) }
            .toApiRes()
    }

    @Operation(summary = "프로젝트 추가")
    @PostMapping("/project")
    fun create(
        @RequestBody request: ProjectResources.Request,
    ): Reply<Long> {
        val id = projectInteraction.register(request.name, request.description)
        return id.toReply()
    }

    @Operation(summary = "프로젝트 조회")
    @GetMapping(value = ["/project/{id}"])
    fun findOne(@PathVariable id: Long): ApiRes<*> {
        val project = projectFinder.findBy(id)
        val reply = ProjectResources.Reply.from(project)
        return ApiRes<Any?>(reply)
    }

    @Operation(summary = "프로젝트에 할당된 사용자 전체 조회")
    @GetMapping("/project/{id}/users")
    fun findUsersProject(@PathVariable id: Long): ApiRes<*> {
        val users = userFinder.findAllByJoinedProjectUsers(id)
        val replies = users.map {
            UserResources.Reply.Detail.from(
                it,
                it.getProjects(),
                it.userAuthorityGrants,
                it.userPersonalGrants,
            )
        }

        return ApiRes<Any?>(replies)
    }

    @Operation(summary = "프로젝트에 할당된 메뉴 네비게이션 전체 조회")
    @GetMapping("/project/{id}/navigations")
    @ResponseBody
    fun findNavigationsProject(@PathVariable id: Long): ApiRes<*> {
        val replyOfMenuNavigations: List<ReplyOfMenuNavigation> =
            projectFinder.findAllByIncludeNavigationsProject(id)
        return ApiRes<Any?>(replyOfMenuNavigations)
    }
}

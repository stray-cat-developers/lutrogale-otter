package io.mustelidae.otter.lutrogale.web.domain.project.api

import io.mustelidae.otter.lutrogale.common.Replies
import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReplies
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
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
    fun findAll(): Replies<ProjectResources.Reply> {
        return projectFinder.findAllByLive()
            .map { ProjectResources.Reply.from(it) }
            .toReplies()
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
    fun findOne(@PathVariable id: Long): Reply<ProjectResources.Reply> {
        val project = projectFinder.findBy(id)
        return ProjectResources.Reply.from(project).toReply()
    }

    @Operation(summary = "프로젝트에 할당된 사용자 전체 조회")
    @GetMapping("/project/{id}/users")
    fun findUsersProject(@PathVariable id: Long): Replies<UserResources.Reply.Detail> {
        val users = userFinder.findAllByJoinedProjectUsers(id)
        return users.map {
            UserResources.Reply.Detail.from(
                it,
                it.getProjects(),
                it.userAuthorityGrants,
                it.userPersonalGrants,
            )
        }.toReplies()
    }

    @Operation(summary = "프로젝트에 할당된 메뉴 네비게이션 전체 조회")
    @GetMapping("/project/{id}/navigations")
    @ResponseBody
    fun findNavigationsProject(@PathVariable id: Long): Replies<ReplyOfMenuNavigation> {
        return projectFinder.findAllByIncludeNavigationsProject(id)
            .toReplies()
    }
}

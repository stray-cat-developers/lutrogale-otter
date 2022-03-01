package io.mustelidae.otter.lutrogale.web.domain.project.api

import io.mustelidae.otter.lutrogale.api.common.Reply
import io.mustelidae.otter.lutrogale.api.common.toReply
import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.commons.toApiRes
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.MenuNavigationResource
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectInteraction
import io.mustelidae.otter.lutrogale.web.domain.user.UserManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created by seooseok on 2016. 9. 20..
 */
@RestController
@RequestMapping("/v1/maintenance")
class ProjectController(
    private val projectInteraction: ProjectInteraction,
    private val userManager: UserManager,
    private val projectFinder: ProjectFinder
) {

    @GetMapping("/projects")
    fun findAll(): ApiRes<*> {
        return projectFinder.findAllByLive()
            .map { ProjectResources.Reply.from(it) }
            .toApiRes()
    }

    @PostMapping("/project")
    fun create(
        @RequestBody request: ProjectResources.Request
    ): Reply<Long> {
        val id = projectInteraction.register(request.name, request.description)
        return id.toReply()
    }

    @GetMapping(value = ["/project/{id}"])
    fun findOne(@PathVariable id: Long): ApiRes<*> {
        val project = projectFinder.findBy(id)
        val reply = ProjectResources.Reply.from(project)
        return ApiRes<Any?>(reply)
    }

    @GetMapping("/project/{id}/users")
    fun findUsersProject(@PathVariable id: Long): ApiRes<*> {
        val users = userManager.findAllByJoinedProjectUsers(id)
        return ApiRes<Any?>(users)
    }

    @GetMapping("/project/{id}/navigations")
    @ResponseBody
    fun findNavigationsProject(@PathVariable id: Long): ApiRes<*> {
        val menuNavigationResources: List<MenuNavigationResource> =
            projectFinder.findAllByIncludeNavigationsProject(id)
        return ApiRes<Any?>(menuNavigationResources)
    }
}

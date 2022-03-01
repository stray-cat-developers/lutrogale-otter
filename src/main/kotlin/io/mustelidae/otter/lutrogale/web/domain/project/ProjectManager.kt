package io.mustelidae.otter.lutrogale.web.domain.project

import io.mustelidae.smoothcoatedotter.web.commons.constant.OsoriConstant.NavigationType
import io.mustelidae.smoothcoatedotter.web.commons.exception.ApplicationException
import io.mustelidae.smoothcoatedotter.web.commons.exception.HumanErr
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigation
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigationManager
import io.mustelidae.smoothcoatedotter.web.domain.navigation.api.MenuNavigationResource
import io.mustelidae.smoothcoatedotter.web.domain.project.repository.ProjectRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Service
@Transactional
class ProjectManager(
    private val projectRepository: ProjectRepository,
    private val menuNavigationManager: MenuNavigationManager
) {

    fun register(name: String, description: String?): Long {
        val project = Project.of(name, description)
        val menuNavigation = MenuNavigation(
            name,
            NavigationType.category,
            "/",
            RequestMethod.GET,
            "1",
            "#"
        )
        project.addBy(menuNavigation)
        return projectRepository.save(project).id!!
    }

    fun findAllByLive(): List<Project> {
        return findAll().filter { it.status }
    }

    protected fun findAll(): List<Project> {
        return projectRepository.findAll()
    }

    fun findBy(id: Long): Project {
        return projectRepository.findByIdOrNull(id) ?: throw ApplicationException(HumanErr.IS_EMPTY)
    }

    fun findByLive(id: Long): Project {
        val project = findBy(id)
        if (!project.status) throw ApplicationException(HumanErr.IS_EXPIRE)
        return project
    }

    fun findByLiveProjectOfApiKey(apiKey: String): Project {
        val project = projectRepository.findByApiKey(apiKey) ?: throw ApplicationException(HumanErr.INVALID_APIKEY)
        if (!project.status)
            throw ApplicationException(HumanErr.IS_EXPIRE)

        return project
    }

    fun findAllByIncludeNavigationsProject(id: Long): List<MenuNavigationResource> {
        val project = findByLive(id)

        return project.menuNavigations.map {
            MenuNavigationResource.of(it, menuNavigationManager.getFullUrl(it))
        }
    }
}

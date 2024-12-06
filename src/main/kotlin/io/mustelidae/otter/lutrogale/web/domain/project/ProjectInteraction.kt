package io.mustelidae.otter.lutrogale.web.domain.project

import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.project.repository.ProjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Service
@Transactional
class ProjectInteraction(
    private val projectRepository: ProjectRepository,
) {

    fun register(name: String, description: String?): Long {
        val project = Project.of(name, description)
        val menuNavigation = MenuNavigation.root()
        project.addBy(menuNavigation)
        return projectRepository.save(project).id!!
    }
}

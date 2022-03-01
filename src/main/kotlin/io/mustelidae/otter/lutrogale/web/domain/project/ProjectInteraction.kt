package io.mustelidae.otter.lutrogale.web.domain.project

import io.mustelidae.otter.lutrogale.web.commons.constant.OsoriConstant.NavigationType
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.project.repository.ProjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Service
@Transactional
class ProjectInteraction(
    private val projectRepository: ProjectRepository
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
}

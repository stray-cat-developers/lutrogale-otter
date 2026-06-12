package io.mustelidae.otter.lutrogale.web.domain.project

import io.mustelidae.otter.lutrogale.config.PreconditionFailException
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation.ListStructure
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
    private val projectFinder: ProjectFinder,
) {
    fun register(
        name: String,
        description: String?,
        listStructure: ListStructure? = ListStructure.TREE,
    ): Long {
        val project = Project.of(name, description, listStructure ?: ListStructure.TREE)
        val menuNavigation = MenuNavigation.root()
        project.addBy(menuNavigation)
        return projectRepository.save(project).id!!
    }

    fun registerSyncSpec(
        projectId: Long,
        spec: Project.SpecType,
        migrationUrl: String,
    ) {
        val project = projectFinder.findBy(projectId)
        if (project.syncEnabled) throw PreconditionFailException("이미 Sync가 설정된 프로젝트입니다.")
        project.setSync(spec, migrationUrl)
        projectRepository.save(project)
    }

    fun startSyncSpec(
        projectId: Long,
        spec: Project.SpecType,
        migrationUrl: String,
    ) {
        val project = projectFinder.findBy(projectId)
        project.setSync(spec, migrationUrl)
        projectRepository.save(project)
    }

    fun stopSyncSpec(projectId: Long) {
        val project = projectFinder.findBy(projectId)
        project.removeSync()
        projectRepository.save(project)
    }
}

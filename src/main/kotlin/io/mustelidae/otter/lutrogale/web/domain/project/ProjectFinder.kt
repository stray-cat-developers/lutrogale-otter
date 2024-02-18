package io.mustelidae.otter.lutrogale.web.domain.project

import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.DataNotFindException
import io.mustelidae.otter.lutrogale.config.HumanException
import io.mustelidae.otter.lutrogale.config.PolicyException
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationInteraction
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.NavigationResources.Reply.ReplyOfMenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.project.repository.ProjectRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProjectFinder(
    private val projectRepository: ProjectRepository,
    private val menuNavigationInteraction: MenuNavigationInteraction,
) {

    fun findAllByLive(): List<Project> {
        return findAll().filter { it.status }
    }

    protected fun findAll(): List<Project> {
        return projectRepository.findAll()
    }

    fun findBy(id: Long): Project {
        return projectRepository.findByIdOrNull(id) ?: throw DataNotFindException("프로젝트 정보가 없습니다.")
    }

    fun findByLive(id: Long): Project {
        val project = findBy(id)
        if (!project.status) {
            throw PolicyException(DefaultError(ErrorCode.PL02, "해당 사용자는 로그인 권한이 만료되었습니다."))
        }
        return project
    }

    fun findByLiveProjectOfApiKey(apiKey: String): Project {
        val project = projectRepository.findByApiKey(apiKey) ?: throw HumanException(DefaultError(ErrorCode.HI03))
        if (!project.status) {
            throw PolicyException(DefaultError(ErrorCode.PL02, "프로젝트가 만료되었습니다."))
        }

        return project
    }

    fun findAllByIncludeNavigationsProject(id: Long): List<ReplyOfMenuNavigation> {
        val project = findByLive(id)

        return project.menuNavigations.map {
            ReplyOfMenuNavigation.from(it, menuNavigationInteraction.getFullUrl(it))
        }
    }
}

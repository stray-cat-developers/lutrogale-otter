package io.mustelidae.otter.lutrogale.web.domain.navigation

import io.mustelidae.otter.lutrogale.api.config.DataNotFindException
import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.HumanErr
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MenuNavigationFinder(
    private val menuNavigationRepository: MenuNavigationRepository
) {
    fun findBy(menuNavigationIds: List<Long>): List<MenuNavigation> {
        return menuNavigationRepository.findByIdIn(menuNavigationIds)
    }

    fun findByLive(menuNavigationIdGroup: List<Long>): List<MenuNavigation> {
        val menuNavigations = this.findBy(menuNavigationIdGroup)

        menuNavigations.forEach {
            if (!it.status) throw ApplicationException(
                HumanErr.IS_EXPIRE
            )
        }

        return menuNavigations
    }

    fun findByTreeId(projectId: Long, treeId: String): MenuNavigation {
        return menuNavigationRepository.findByProjectIdAndTreeId(projectId, treeId)
            ?: throw ApplicationException(HumanErr.IS_EMPTY)
    }

    fun findByMenuNavigationId(projectId: Long, menuNavigationId: Long): MenuNavigation {
        return menuNavigationRepository.findByProjectIdAndId(projectId, menuNavigationId) ?: throw DataNotFindException(menuNavigationId, "해당 노드가 없습니다.")
    }

    fun findByLive(projectId: Long, menuNavigationIdGroup: List<Long>): List<MenuNavigation> {
        return menuNavigationRepository.findByStatusTrueAndProjectIdAndIdIn(projectId, menuNavigationIdGroup)
    }

    fun findByLive(menuNavigationId: Long): MenuNavigation {
        val menuNavigation: MenuNavigation = menuNavigationRepository.findByIdOrNull(menuNavigationId)
            ?: throw ApplicationException(HumanErr.IS_EMPTY)
        if (!menuNavigation.status)
            throw ApplicationException(HumanErr.IS_EXPIRE)
        return menuNavigation
    }
}

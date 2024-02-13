package io.mustelidae.otter.lutrogale.web.domain.navigation

import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.DataNotFindException
import io.mustelidae.otter.lutrogale.config.PolicyException
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MenuNavigationFinder(
    private val menuNavigationRepository: MenuNavigationRepository,
) {
    fun findBy(menuNavigationIds: List<Long>): List<MenuNavigation> {
        return menuNavigationRepository.findByIdIn(menuNavigationIds)
    }

    fun findByLive(menuNavigationIdGroup: List<Long>): List<MenuNavigation> {
        val menuNavigations = this.findBy(menuNavigationIdGroup)

        menuNavigations.forEach {
            if (!it.status) {
                throw PolicyException(DefaultError(ErrorCode.PL02, "해당 사용자는 로그인 권한이 만료되었습니다."))
            }
        }

        return menuNavigations
    }

    fun findByTreeId(projectId: Long, treeId: String): MenuNavigation {
        return menuNavigationRepository.findByProjectIdAndTreeId(projectId, treeId)
            ?: throw DataNotFindException("메뉴네비게이션이 없습니다.")
    }

    fun findByMenuNavigationId(projectId: Long, menuNavigationId: Long): MenuNavigation {
        return menuNavigationRepository.findByProjectIdAndId(projectId, menuNavigationId) ?: throw DataNotFindException(menuNavigationId, "해당 노드가 없습니다.")
    }

    fun findByLive(projectId: Long, menuNavigationIdGroup: List<Long>): List<MenuNavigation> {
        return menuNavigationRepository.findByStatusTrueAndProjectIdAndIdIn(projectId, menuNavigationIdGroup)
    }

    fun findByLive(menuNavigationId: Long): MenuNavigation {
        val menuNavigation: MenuNavigation = menuNavigationRepository.findByIdOrNull(menuNavigationId)
            ?: throw DataNotFindException("사용자 정보가 없습니다.")
        if (!menuNavigation.status) {
            throw PolicyException(DefaultError(ErrorCode.PL02, "해당 사용자는 로그인 권한이 만료되었습니다."))
        }
        return menuNavigation
    }
}

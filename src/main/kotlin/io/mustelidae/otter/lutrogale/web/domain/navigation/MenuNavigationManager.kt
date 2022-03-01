package io.mustelidae.otter.lutrogale.web.domain.navigation

import io.mustelidae.otter.lutrogale.web.commons.constant.OsoriConstant.NavigationType
import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.HumanErr
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod
import java.util.Optional
import java.util.function.Consumer

/**
 * Created by seooseok on 2016. 10. 11..
 */
@Service
class MenuNavigationManager(
    private val menuNavigationRepository: MenuNavigationRepository
) {

    fun findBy(menuNavigationIds: List<Long>): List<MenuNavigation> {
        return menuNavigationRepository.findByIdIn(menuNavigationIds)
    }

    fun findByLive(menuNavigationIdGroup: List<Long>): List<MenuNavigation> {
        val menuNavigations = this.findBy(menuNavigationIdGroup)
        menuNavigations.forEach(
            Consumer { menuNavigation: MenuNavigation ->
                if (!menuNavigation.status) throw ApplicationException(
                    HumanErr.IS_EXPIRE
                )
            }
        )
        return menuNavigations
    }

    fun findBy(projectId: Long, treeId: String): MenuNavigation {
        return menuNavigationRepository.findByProjectIdAndTreeId(projectId, treeId)
            ?: throw ApplicationException(HumanErr.IS_EMPTY)
    }

    fun findBy(projectId: Long, nodeId: Long): MenuNavigation {
        val menuNavigation =
            Optional.ofNullable<MenuNavigation>(menuNavigationRepository.findByProjectIdAndId(projectId, nodeId))
        if (!menuNavigation.isPresent) throw ApplicationException(HumanErr.IS_EMPTY)
        return menuNavigation.get()
    }

    fun findByLive(projectId: Long, menuNavigationIdGroup: List<Long>): List<MenuNavigation> {
        return menuNavigationRepository.findByStatusTrueAndProjectIdAndIdIn(projectId, menuNavigationIdGroup)
    }

    fun findByLive(menuNavigationId: Long): MenuNavigation {
        val menuNavigation: MenuNavigation = menuNavigationRepository.findByIdOrNull(menuNavigationId)
            ?: throw ApplicationException(HumanErr.IS_EMPTY)
        if (!menuNavigation.status) throw ApplicationException(HumanErr.IS_EXPIRE)
        return menuNavigation
    }

    fun getFullUrl(menuNavigation: MenuNavigation?): String {
        return if ("#" == menuNavigation!!.parentTreeId) menuNavigation.uriBlock else getFullUrl(
            menuNavigation.parentMenuNavigation
        ) + menuNavigation.uriBlock
    }

    fun modify(
        menuNavigationId: Long,
        name: String,
        type: NavigationType,
        methodType: RequestMethod,
        uriBlock: String
    ) {
        val menuNavigation = this.findByLive(menuNavigationId).apply {
            this.type = type
            this.name = name
            this.uriBlock = uriBlock
            this.methodType = methodType
        }
        menuNavigationRepository.save(menuNavigation)
    }
}

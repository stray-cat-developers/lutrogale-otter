package io.mustelidae.otter.lutrogale.web.domain.navigation

import io.mustelidae.otter.lutrogale.common.Constant.NavigationType
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Created by seooseok on 2016. 10. 11..
 */
@Service
@Transactional
class MenuNavigationInteraction(
    private val menuNavigationRepository: MenuNavigationRepository,
    private val menuNavigationFinder: MenuNavigationFinder,
) {

    fun getFullUrl(menuNavigation: MenuNavigation?): String {
        val fullUrl = if ("#" == menuNavigation!!.parentTreeId) {
            "/"
        } else {
            getFullUrl(menuNavigation.parentMenuNavigation) + menuNavigation.uriBlock
        }

        return fullUrl.replace("//*".toRegex(), "/")
    }

    fun modify(
        menuNavigationId: Long,
        name: String,
        type: NavigationType,
        methodType: RequestMethod,
        uriBlock: String,
    ) {
        val menuNavigation = menuNavigationFinder.findByLive(menuNavigationId).apply {
            this.type = type
            this.name = name
            this.uriBlock = uriBlock
            this.methodType = methodType
        }
        menuNavigationRepository.save(menuNavigation)
    }
}

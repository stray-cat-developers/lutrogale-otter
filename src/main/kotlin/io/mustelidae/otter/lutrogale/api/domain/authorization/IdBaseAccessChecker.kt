package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResource
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AuthenticationCheckResource
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationManager

class IdBaseAccessChecker(
    private val menuNavigationManager: MenuNavigationManager
) : AccessChecker {

    override fun validate(
        sourceNavigationGroup: List<MenuNavigation>,
        authenticationCheckResource: AuthenticationCheckResource
    ): List<AccessResource> {
        val accessResources: MutableList<AccessResource> = ArrayList()
        val targetMenuNavigationGroup: List<MenuNavigation> =
            menuNavigationManager.findBy(authenticationCheckResource.menuNavigationIdGroup!!)
        for (menuNavigation in targetMenuNavigationGroup) {
            if (sourceNavigationGroup.contains(menuNavigation) && menuNavigation.project!!.apiKey == authenticationCheckResource.apiKey) accessResources.add(
                AccessResource.ofAccept(
                    menuNavigation.id!!
                )
            ) else {
                accessResources.add(AccessResource.ofDenied(menuNavigation.id!!, "접근 할 수 없는 메뉴입니다."))
            }
        }
        return accessResources
    }
}

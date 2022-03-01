package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AuthenticationCheckResource
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationManager

class IdBaseAccessChecker(
    private val menuNavigationManager: MenuNavigationManager
) : AccessChecker {

    override fun validate(
        sourceNavigationGroup: List<MenuNavigation>,
        authenticationCheckResource: AuthenticationCheckResource
    ): List<AccessResources.Reply.AccessState> {
        val accessStates: MutableList<AccessResources.Reply.AccessState> = ArrayList()
        val targetMenuNavigationGroup: List<MenuNavigation> =
            menuNavigationManager.findBy(authenticationCheckResource.menuNavigationIdGroup!!)
        for (menuNavigation in targetMenuNavigationGroup) {
            if (sourceNavigationGroup.contains(menuNavigation) && menuNavigation.project!!.apiKey == authenticationCheckResource.apiKey) accessStates.add(
                AccessResources.Reply.AccessState.ofAccept(
                    menuNavigation.id!!
                )
            ) else {
                accessStates.add(AccessResources.Reply.AccessState.ofDenied(menuNavigation.id!!, "접근 할 수 없는 메뉴입니다."))
            }
        }
        return accessStates
    }
}

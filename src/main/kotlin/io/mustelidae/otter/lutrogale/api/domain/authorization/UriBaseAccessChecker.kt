package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AuthenticationResources
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationInteraction
import org.springframework.util.AntPathMatcher

class UriBaseAccessChecker(
    private val menuNavigationInteraction: MenuNavigationInteraction
) : AccessChecker {

    override fun validate(
        sourceNavigationGroup: List<MenuNavigation>,
        accessGrant: AuthenticationResources.Reply.AccessGrant
    ): List<AccessResources.Reply.AccessState> {
        val accessStates: MutableList<AccessResources.Reply.AccessState> = ArrayList()
        val antPathMatcher = AntPathMatcher()
        for (accessUri in accessGrant.accessUriGroup!!) {
            var isMatch = false
            var matchedSourceUrl: String? = null
            for (menuNavigation in sourceNavigationGroup) {
                val sourceUrl: String = menuNavigationInteraction.getFullUrl(menuNavigation)
                if (antPathMatcher.match(sourceUrl, accessUri.uri) &&
                    menuNavigation.methodType == accessUri.requestMethod && menuNavigation.project!!.apiKey == accessGrant.apiKey
                ) {
                    isMatch = true
                    matchedSourceUrl = sourceUrl
                    break
                }
            }
            if (isMatch) {
                accessStates.add(AccessResources.Reply.AccessState.ofAccept(matchedSourceUrl!!))
            } else accessStates.add(AccessResources.Reply.AccessState.ofDenied(accessUri.uri, "not matched"))
        }
        return accessStates
    }
}

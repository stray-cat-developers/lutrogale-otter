package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationInteraction
import org.springframework.http.server.PathContainer
import org.springframework.web.util.pattern.PathPatternParser

class UriBaseAccessChecker(
    private val menuNavigationInteraction: MenuNavigationInteraction,
) : AccessChecker {

    override fun validate(
        sourceNavigationGroup: List<MenuNavigation>,
        accessGrant: AccessGrant,
    ): List<AccessResources.Reply.AccessState> {
        val accessStates: MutableList<AccessResources.Reply.AccessState> = ArrayList()
        val pathPatternParser = PathPatternParser()
        for (accessUri in accessGrant.accessUriGroup!!) {
            var isMatch = false
            var matchedSourceUrl: String? = null
            for (menuNavigation in sourceNavigationGroup) {
                val sourceUrl: String = menuNavigationInteraction.getFullUrl(menuNavigation)
                val pathPattern = pathPatternParser.parse(sourceUrl)
                val path = PathContainer.parsePath(accessUri.uri)

                if (pathPattern.matches(path) &&
                    menuNavigation.methodType == accessUri.methodType && menuNavigation.project!!.apiKey == accessGrant.apiKey
                ) {
                    isMatch = true
                    matchedSourceUrl = sourceUrl
                    break
                }
            }
            if (isMatch) {
                accessStates.add(AccessResources.Reply.AccessState.ofAccept(matchedSourceUrl!!))
            } else {
                accessStates.add(AccessResources.Reply.AccessState.ofDenied(accessUri.uri, "not matched"))
            }
        }
        return accessStates
    }
}

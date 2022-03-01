package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResource
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AuthenticationCheckResource
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationManager
import org.springframework.util.AntPathMatcher


class UriBaseAccessChecker(
    private val menuNavigationManager: MenuNavigationManager
) : AccessChecker {

    override fun validate(
        sourceNavigationGroup: List<MenuNavigation>,
        authenticationCheckResource: AuthenticationCheckResource
    ): List<AccessResource> {
        val accessResources: MutableList<AccessResource> = ArrayList()
        val antPathMatcher = AntPathMatcher()
        for (accessUri in authenticationCheckResource.accessUriGroup!!) {
            var isMatch = false
            var matchedSourceUrl: String? = null
            for (menuNavigation in sourceNavigationGroup) {
                val sourceUrl: String = menuNavigationManager.getFullUrl(menuNavigation)
                if (antPathMatcher.match(sourceUrl, accessUri.uri) &&
                    menuNavigation.methodType == accessUri.requestMethod && menuNavigation.project!!.apiKey == authenticationCheckResource.apiKey
                ) {
                    isMatch = true
                    matchedSourceUrl = sourceUrl
                    break
                }
            }
            if (isMatch) {
                accessResources.add(AccessResource.ofAccept(matchedSourceUrl!!))
            } else accessResources.add(AccessResource.ofDenied(accessUri.uri, "not matched"))
        }
        return accessResources
    }
}

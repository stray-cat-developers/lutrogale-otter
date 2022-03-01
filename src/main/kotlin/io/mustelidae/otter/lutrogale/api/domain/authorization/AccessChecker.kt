package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResource
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AuthenticationCheckResource
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation

/**
 * Created by seooseok on 2016. 10. 27..
 * 권한 요청 체크
 */
interface AccessChecker {
    fun validate(
        sourceNavigationGroup: List<MenuNavigation>,
        authenticationCheckResource: AuthenticationCheckResource
    ): List<AccessResource>
}

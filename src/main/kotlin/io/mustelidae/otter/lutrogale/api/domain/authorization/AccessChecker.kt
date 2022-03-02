package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AuthenticationResources
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation

/**
 * Created by seooseok on 2016. 10. 27..
 * 권한 요청 체크
 */
interface AccessChecker {
    fun validate(
        sourceNavigationGroup: List<MenuNavigation>,
        accessGrant: AuthenticationResources.Reply.AccessGrant
    ): List<AccessResources.Reply.AccessState>
}

package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.common.Constant.AuthenticationCheckType
import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.ProcessErr
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationFinder
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationInteraction
import org.springframework.stereotype.Service

/**
 * Created by seooseok on 2016. 10. 27..
 * 요청 방법 확인
 */
@Service
class AccessCheckerHandler(
    private val menuNavigationInteraction: MenuNavigationInteraction,
    private val menuNavigationFinder: MenuNavigationFinder
) {

    fun handle(checkType: AuthenticationCheckType): AccessChecker {
        if (checkType === AuthenticationCheckType.id)
            return IdBaseAccessChecker(menuNavigationFinder)
        if (checkType === AuthenticationCheckType.uri)
            return UriBaseAccessChecker(menuNavigationInteraction)
        throw ApplicationException(ProcessErr.WRONG_DEVELOP_PROCESS)
    }
}

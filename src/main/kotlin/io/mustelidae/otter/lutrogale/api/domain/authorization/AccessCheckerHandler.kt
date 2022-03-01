package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.smoothcoatedotter.web.commons.constant.OsoriConstant.AuthenticationCheckType
import io.mustelidae.smoothcoatedotter.web.commons.exception.ApplicationException
import io.mustelidae.smoothcoatedotter.web.commons.exception.ProcessErr
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigationManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * Created by seooseok on 2016. 10. 27..
 * 요청 방법 확인
 */
@Service
class AccessCheckerHandler(
    private val menuNavigationManager: MenuNavigationManager
) {

    fun handle(checkType: AuthenticationCheckType): AccessChecker {
        if (checkType === AuthenticationCheckType.id)
            return IdBaseAccessChecker(menuNavigationManager)
        if (checkType === AuthenticationCheckType.uri)
            return UriBaseAccessChecker(menuNavigationManager)
        throw ApplicationException(ProcessErr.WRONG_DEVELOP_PROCESS)
    }
}

package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.common.Constant.AuthenticationCheckType
import io.mustelidae.otter.lutrogale.config.DevelopMistakeException
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
    private val menuNavigationFinder: MenuNavigationFinder,
) {

    fun handle(checkType: AuthenticationCheckType): AccessChecker {
        if (checkType === AuthenticationCheckType.ID) {
            return IdBaseAccessChecker(menuNavigationFinder)
        }
        if (checkType === AuthenticationCheckType.URI) {
            return UriBaseAccessChecker(menuNavigationInteraction)
        }
        throw DevelopMistakeException("해당 요청이 잘못된 작업을 수행하고 있습니다.")
    }
}

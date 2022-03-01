package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import com.google.common.base.Strings
import io.mustelidae.otter.lutrogale.web.commons.constant.OsoriConstant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Created by seooseok on 2016. 10. 19..
 */
class MenuNavigationResource(
    val id: Long,
    val type: OsoriConstant.NavigationType,
    val name: String,
    val uriBlock: String,
    val methodType: RequestMethod,
    val fullUrl: String? = null
) {

    companion object {
        fun from(menuNavigation: MenuNavigation, fullUrl: String): MenuNavigationResource {
            var fullUrl = fullUrl
            if (!Strings.isNullOrEmpty(fullUrl)) fullUrl = fullUrl.replace("//*".toRegex(), "/")

            return menuNavigation.run {
                MenuNavigationResource(
                    id!!, type, name, uriBlock, methodType, fullUrl
                )
            }
        }
    }
}

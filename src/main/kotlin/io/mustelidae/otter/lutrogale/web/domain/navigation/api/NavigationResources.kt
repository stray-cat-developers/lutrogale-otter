package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import io.mustelidae.otter.lutrogale.web.commons.constant.OsoriConstant
import org.springframework.web.bind.annotation.RequestMethod

class NavigationResources {

    class Modify(
        val name: String,
        val type: OsoriConstant.NavigationType,
        val methodType: RequestMethod,
        val uriBlock: String
    )
}

package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import io.mustelidae.otter.lutrogale.web.commons.constant.OsoriConstant
import org.springframework.web.bind.annotation.RequestMethod

class MenuTreeResources {

    class Request {

        class Branch(
            val treeId: String,
            val parentTreeId: String,
            val name: String,
            val uriBlock: String,
            val type: OsoriConstant.NavigationType,
            val methodType: RequestMethod
        )
    }
}
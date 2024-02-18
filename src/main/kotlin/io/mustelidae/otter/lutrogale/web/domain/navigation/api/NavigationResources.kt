package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.bind.annotation.RequestMethod

class NavigationResources {

    @Schema(name = "Lutrogale.Navigation.Modify")
    class Modify(
        val name: String,
        val type: Constant.NavigationType,
        val methodType: RequestMethod,
        val uriBlock: String,
    )

    class Reply {
        @Schema(name = "Lutrogale.Navigation.Reply.MenuNavigation")
        class ReplyOfMenuNavigation(
            val id: Long,
            val type: Constant.NavigationType,
            val name: String,
            val uriBlock: String,
            val methodType: RequestMethod,
            val fullUrl: String? = null,
        ) {

            companion object {
                fun from(menuNavigation: MenuNavigation, fullUrl: String): ReplyOfMenuNavigation {
                    return menuNavigation.run {
                        ReplyOfMenuNavigation(
                            id!!,
                            type,
                            name,
                            uriBlock,
                            methodType,
                            fullUrl,
                        )
                    }
                }
            }
        }
    }
}

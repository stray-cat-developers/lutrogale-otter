package io.mustelidae.otter.lutrogale.web.domain.grant.api

import io.mustelidae.smoothcoatedotter.utils.toDateString
import io.mustelidae.smoothcoatedotter.web.commons.constant.OsoriConstant
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigation
import java.time.LocalDateTime

/**
 * Created by seooseok on 2016. 10. 10..
 */

class PersonalGrantResource(
    val id: Long,
    val type: OsoriConstant.NavigationType,
    val name: String,
    val uriBlock: String,
    val regDate: String,
    val projectId: Long,
    val projectName: String,
    val fullUrl: String? = null
) {

    companion object {
        fun of(menuNavigation: MenuNavigation, regDate: LocalDateTime): PersonalGrantResource {
            return menuNavigation.run {
                PersonalGrantResource(
                    id!!,
                    type,
                    name,
                    uriBlock,
                    regDate.toDateString(),
                    project!!.id!!,
                    project!!.name
                )
            }
        }

        fun of(menuNavigation: MenuNavigation, regDate: LocalDateTime, fullUrl: String?): PersonalGrantResource {
            return menuNavigation.run {
                PersonalGrantResource(
                    id!!,
                    type,
                    name,
                    uriBlock,
                    regDate.toDateString(),
                    project!!.id!!,
                    project!!.name,
                    fullUrl
                )
            }
        }
    }
}

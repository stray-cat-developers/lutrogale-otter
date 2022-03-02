package io.mustelidae.otter.lutrogale.web.domain.grant.api

import io.mustelidae.otter.lutrogale.utils.toDateString
import io.mustelidae.otter.lutrogale.web.commons.constant.OsoriConstant
import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityDefinition
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class UserGrantResources {

    class Reply {

        @Schema(name = "UserGrant.Reply.AuthorityGrant")
        data class AuthorityGrant(
            val id: Long,
            val name: String,
            val regDate: String,
            val projectId: Long,
            val projectName: String
        ) {

            companion object {
                fun from(authorityDefinition: AuthorityDefinition, regDate: LocalDateTime): AuthorityGrant {
                    return authorityDefinition.run {
                        AuthorityGrant(
                            id!!,
                            name,
                            regDate.toDateString(),
                            project!!.id!!,
                            project!!.name
                        )
                    }
                }

                fun from(authorityDefinition: AuthorityDefinition): AuthorityGrant {
                    return authorityDefinition.run {
                        AuthorityGrant(
                            id!!,
                            name,
                            createdAt!!.toDateString(),
                            project!!.id!!,
                            project!!.name
                        )
                    }
                }
            }
        }

        @Schema(name = "UserGrant.Reply.PersonalGrant")
        data class PersonalGrant(
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
                fun from(menuNavigation: MenuNavigation, regDate: LocalDateTime): PersonalGrant {
                    return menuNavigation.run {
                        PersonalGrant(
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

                fun from(menuNavigation: MenuNavigation, regDate: LocalDateTime, fullUrl: String?): PersonalGrant {
                    return menuNavigation.run {
                        PersonalGrant(
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
    }
}

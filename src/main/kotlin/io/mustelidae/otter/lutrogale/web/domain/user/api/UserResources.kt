package io.mustelidae.otter.lutrogale.web.domain.user.api

import io.mustelidae.otter.lutrogale.web.domain.grant.UserAuthorityGrant
import io.mustelidae.otter.lutrogale.web.domain.grant.UserPersonalGrant
import io.mustelidae.otter.lutrogale.web.domain.grant.api.UserGrantResources.Reply.AuthorityGrant
import io.mustelidae.otter.lutrogale.web.domain.grant.api.UserGrantResources.Reply.PersonalGrant
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.mustelidae.otter.lutrogale.web.domain.project.api.ProjectResources
import io.mustelidae.otter.lutrogale.web.domain.user.User
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class UserResources {

    @Schema(name = "Lutrogale.User.Request")
    class Request(
        val email: String,
        val name: String,
        val isPrivacy: Boolean,
        val department: String? = null,
    )

    class Modify {

        @Schema(name = "Lutrogale.User.Modify.Info")
        data class Info(
            val isPrivacy: Boolean,
            val name: String,
            val department: String,
        )

        @Schema(name = "Lutrogale.User.Modify.UserState")
        data class UserState(
            val status: String,
        ) {
            fun getStatus(): User.Status {
                return User.Status.valueOf(this.status.uppercase())
            }
        }
    }

    class Reply {
        @Schema(name = "Lutrogale.User.Simple")
        data class Simple(
            val id: Long,
            val email: String,
            val name: String,
            val accessPrivacyInformation: Boolean,
            val regDate: LocalDateTime,
            val status: User.Status,
            val department: String? = null,
        ) {
            companion object {
                fun from(user: User): Simple {
                    return user.run {
                        Simple(
                            id!!,
                            email,
                            name,
                            isPrivacy,
                            createdAt!!,
                            status,
                            department,
                        )
                    }
                }
            }
        }

        @Schema(name = "Lutrogale.User.Detail")
        data class Detail(
            val id: Long,
            val email: String,
            val name: String,
            val accessPrivacyInformation: Boolean,
            val regDate: LocalDateTime,
            val status: User.Status,
            val department: String? = null,
            val projects: List<ProjectResources.Reply>? = null,
            val authorityDefinitions: List<AuthorityGrant>? = null,
            val menuNavigations: List<PersonalGrant>? = null,
        ) {
            companion object {
                fun from(
                    user: User,
                    projects: List<Project>,
                    userAuthorityGrants: List<UserAuthorityGrant>,
                    userPersonalGrants: List<UserPersonalGrant>,
                ): Detail {
                    val repliesOfProject = projects.map { ProjectResources.Reply.from(it) }
                    val authorityGrants = userAuthorityGrants.map { AuthorityGrant.from(it.authorityDefinition!!, it.createdAt!!) }
                    val personalGrants = userPersonalGrants.map { PersonalGrant.from(it.menuNavigation!!, it.createdAt!!) }

                    return user.run {
                        Detail(
                            id!!,
                            email,
                            name,
                            isPrivacy,
                            createdAt!!,
                            status,
                            department,
                            repliesOfProject,
                            authorityGrants,
                            personalGrants,
                        )
                    }
                }
            }
        }
    }
}

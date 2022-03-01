package io.mustelidae.otter.lutrogale.web.domain.user.api

import io.mustelidae.smoothcoatedotter.web.domain.grant.UserAuthorityGrant
import io.mustelidae.smoothcoatedotter.web.domain.grant.UserPersonalGrant
import io.mustelidae.smoothcoatedotter.web.domain.grant.api.AuthorityGrantResource
import io.mustelidae.smoothcoatedotter.web.domain.grant.api.PersonalGrantResource
import io.mustelidae.smoothcoatedotter.web.domain.project.Project
import io.mustelidae.smoothcoatedotter.web.domain.project.api.ProjectResources.Reply
import io.mustelidae.smoothcoatedotter.web.domain.user.User
import java.time.LocalDateTime

/**
 * Created by seooseok on 2016. 10. 6..
 */
class UserResource(
    val id: Long,
    val email: String,
    val name: String,
    val accessPrivacyInformation: Boolean,
    val regDate: LocalDateTime,
    val status: User.Status,
    val department: String? = null,
    val projects: List<Reply>? = null,
    val authorityDefinitions: List<AuthorityGrantResource>? = null,
    val menuNavigations: List<PersonalGrantResource>? = null
) {

    companion object {
        fun of(user: User): UserResource {
            return user.run {
                UserResource(
                    id!!,
                    email,
                    name,
                    isPrivacy,
                    createdAt!!,
                    status
                )
            }
        }

        fun ofDetail(
            user: User,
            projects: List<Project>,
            userAuthorityGrants: List<UserAuthorityGrant>,
            userPersonalGrants: List<UserPersonalGrant>
        ): UserResource {

            val repliesOfProject = projects.map { Reply.from(it) }
            val authorityGrantResources = userAuthorityGrants.map { AuthorityGrantResource.of(it.authorityDefinition!!, it.createdAt!!) }
            val personalGrantResources = userPersonalGrants.map { PersonalGrantResource.of(it.menuNavigation!!, it.createdAt!!) }

            return user.run {
                UserResource(
                    id!!,
                    email,
                    name,
                    isPrivacy,
                    createdAt!!,
                    status,
                    department,
                    repliesOfProject,
                    authorityGrantResources,
                    personalGrantResources
                )
            }
        }
    }
}

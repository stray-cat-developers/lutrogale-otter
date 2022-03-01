package io.mustelidae.otter.lutrogale.web.domain.user.repository

import io.mustelidae.otter.lutrogale.web.domain.authority.QAuthorityDefinition.authorityDefinition
import io.mustelidae.otter.lutrogale.web.domain.grant.QUserAuthorityGrant.userAuthorityGrant
import io.mustelidae.otter.lutrogale.web.domain.project.QProject.project
import io.mustelidae.otter.lutrogale.web.domain.user.QUser.user
import io.mustelidae.otter.lutrogale.web.domain.user.User
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class UserDSLRepository : QuerydslRepositorySupport(User::class.java) {

    fun findAllByJoinedProjectUsers(projectId: Long): List<User> {
        return from(user)
            .innerJoin(user.userAuthorityGrants, userAuthorityGrant).fetchJoin()
            .innerJoin(userAuthorityGrant.authorityDefinition, authorityDefinition).fetchJoin()
            .innerJoin(authorityDefinition.project, project).fetchJoin()
            .where(
                project.id.eq(projectId),
                user.status.eq(User.Status.allow),
                userAuthorityGrant.status.isTrue,
                authorityDefinition.status.isTrue
            ).fetch()
            .distinct()
    }
}

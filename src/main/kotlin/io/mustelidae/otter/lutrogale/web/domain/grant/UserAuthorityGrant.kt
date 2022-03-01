package io.mustelidae.otter.lutrogale.web.domain.grant

import io.mustelidae.smoothcoatedotter.api.common.Audit
import io.mustelidae.smoothcoatedotter.web.domain.user.User
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Created by seooseok on 2016. 10. 5..
 */
@Entity
@Table(name = "UserHasAuthorityDefinition")
class UserAuthorityGrant : Audit() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", updatable = false, nullable = false)
    var user: User? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorityDefinitionId", updatable = false, nullable = false)
    var authorityDefinition: AuthorityDefinition? = null
        protected set

    var status = true

    fun setBy(user: User) {
        this.user = user
        if (user.userAuthorityGrants.contains(this).not())
            user.addBy(this)
    }

    fun setBy(authorityDefinition: AuthorityDefinition) {
        this.authorityDefinition = authorityDefinition
        if (authorityDefinition.userAuthorityGrants.contains(this).not())
            authorityDefinition.addBy(this)
    }

    fun expire() {
        status = false
    }
}

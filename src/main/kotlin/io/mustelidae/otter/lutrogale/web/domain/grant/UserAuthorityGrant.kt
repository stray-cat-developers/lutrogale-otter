package io.mustelidae.otter.lutrogale.web.domain.grant

import io.mustelidae.otter.lutrogale.common.Audit
import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityDefinition
import io.mustelidae.otter.lutrogale.web.domain.user.User
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode

/**
 * Created by seooseok on 2016. 10. 5..
 */
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "UserHasAuthorityDefinition")
class UserAuthorityGrant : Audit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", updatable = false, nullable = false)
    var user: User? = null
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorityDefinitionId", updatable = false, nullable = false)
    var authorityDefinition: AuthorityDefinition? = null
        private set

    var status = true

    fun setBy(user: User) {
        this.user = user
        if (user.userAuthorityGrants.contains(this).not()) {
            user.addBy(this)
        }
    }

    fun setBy(authorityDefinition: AuthorityDefinition) {
        this.authorityDefinition = authorityDefinition
        if (authorityDefinition.userAuthorityGrants.contains(this).not()) {
            authorityDefinition.addBy(this)
        }
    }

    fun expire() {
        status = false
    }
}

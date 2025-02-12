package io.mustelidae.otter.lutrogale.web.domain.navigation

import io.mustelidae.otter.lutrogale.common.Audit
import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityDefinition
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
 * Created by seooseok on 2016. 9. 29..
 * 권한 그룹과 네비게이션 매핑
 */
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "AuthorityDefinitionHasMenuNavigation")
class AuthorityNavigationUnit : Audit() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorityDefinitionId", updatable = false, nullable = false)
    var authorityDefinition: AuthorityDefinition? = null
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuNavigationId", updatable = false, nullable = false)
    var menuNavigation: MenuNavigation? = null
        private set

    private var status = true

    fun expire() {
        status = false
    }

    fun setBy(authorityDefinition: AuthorityDefinition) {
        this.authorityDefinition = authorityDefinition

        if (authorityDefinition.authorityNavigationUnits.contains(this).not()) {
            authorityDefinition.addBy(this)
        }
    }

    fun setBy(menuNavigation: MenuNavigation) {
        this.menuNavigation = menuNavigation
        if (menuNavigation.authorityNavigationUnits.contains(this).not()) {
            menuNavigation.addBy(this)
        }
    }
}

package io.mustelidae.otter.lutrogale.web.domain.grant

import io.mustelidae.smoothcoatedotter.api.common.Audit
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigation
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Created by seooseok on 2016. 9. 29..
 * 권한 그룹과 네비게이션 매핑
 */
@Entity
@Table(name = "AuthorityDefinitionHasMenuNavigation")
class AuthorityNavigationUnit : Audit() {

    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorityDefinitionId", updatable = false, nullable = false)
    var authorityDefinition: AuthorityDefinition? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuNavigationId", updatable = false, nullable = false)
    var menuNavigation: MenuNavigation? = null
        protected set

    private var status = true

    fun expire() {
        status = false
    }

    fun setBy(authorityDefinition: AuthorityDefinition) {
        this.authorityDefinition = authorityDefinition

        if (authorityDefinition.authorityNavigationUnits.contains(this).not())
            authorityDefinition.addBy(this)
    }

    fun setBy(menuNavigation: MenuNavigation) {
        this.menuNavigation = menuNavigation
        if (menuNavigation.authorityNavigationUnits.contains(this).not())
            menuNavigation.addBy(this)
    }
}

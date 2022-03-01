package io.mustelidae.otter.lutrogale.web.domain.grant

import io.mustelidae.smoothcoatedotter.api.common.Audit
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigation
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
@Table(name = "UserHasMenuNavigation")
class UserPersonalGrant : Audit() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false, updatable = false)
    var user: User? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuNavigationId", nullable = false, updatable = false)
    var menuNavigation: MenuNavigation? = null
        protected set

    var status = true

    fun setBy(user: User) {
        this.user = user
        if (user.userPersonalGrants.contains(this).not())
            user.addBy(this)
    }

    fun setBy(menuNavigation: MenuNavigation) {
        this.menuNavigation = menuNavigation
        if (menuNavigation.userPersonalGrants.contains(this).not())
            menuNavigation.addBy(this)
    }

    fun expire() {
        status = false
    }
}

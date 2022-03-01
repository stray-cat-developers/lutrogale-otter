package io.mustelidae.otter.lutrogale.web.domain.navigation

import io.mustelidae.otter.lutrogale.api.common.Audit
import io.mustelidae.otter.lutrogale.web.commons.constant.OsoriConstant
import io.mustelidae.otter.lutrogale.web.domain.grant.AuthorityNavigationUnit
import io.mustelidae.otter.lutrogale.web.domain.grant.UserPersonalGrant
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import org.springframework.web.bind.annotation.RequestMethod
import javax.persistence.CascadeType.ALL
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType.STRING
import javax.persistence.Enumerated
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Entity
class MenuNavigation(
    @Column(nullable = false, length = 50)
    var name: String,
    @Enumerated(STRING)
    var type: OsoriConstant.NavigationType,
    var uriBlock: String,
    @Enumerated(STRING)
    var methodType: RequestMethod,
    var treeId: String,
    var parentTreeId: String,
) : Audit() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    @ManyToOne
    @JoinColumn(name = "projectId")
    var project: Project? = null
        protected set

    @ManyToOne
    @JoinColumn(name = "parentId")
    var parentMenuNavigation: MenuNavigation? = null
        protected set

    @OneToMany(mappedBy = "parentMenuNavigation", fetch = LAZY, cascade = [ALL])
    var menuNavigations: MutableList<MenuNavigation> = ArrayList()
        protected set

    @OneToMany(mappedBy = "menuNavigation", fetch = LAZY, cascade = [ALL])
    var authorityNavigationUnits: MutableList<AuthorityNavigationUnit> = arrayListOf()
        protected set

    @OneToMany(mappedBy = "menuNavigation", fetch = LAZY, cascade = [ALL])
    var userPersonalGrants: MutableList<UserPersonalGrant> = arrayListOf()
        protected set

    var status = true
        protected set

    fun setBy(parentMenuNavigation: MenuNavigation) {
        this.parentMenuNavigation = parentMenuNavigation
        if (!parentMenuNavigation.menuNavigations.contains(this))
            parentMenuNavigation.addBy(this)
    }

    fun setBy(project: Project) {
        this.project = project
        if (!project.menuNavigations.contains(this))
            project.addBy(this)
    }

    fun addBy(authorityNavigationUnit: AuthorityNavigationUnit) {
        authorityNavigationUnits.add(authorityNavigationUnit)
        if (authorityNavigationUnit.menuNavigation != this)
            authorityNavigationUnit.setBy(this)
    }

    fun addBy(menuNavigation: MenuNavigation) {
        menuNavigations.add(menuNavigation)
        if (this != menuNavigation.parentMenuNavigation)
            menuNavigation.setBy(this)
    }

    fun addBy(userPersonalGrant: UserPersonalGrant) {
        userPersonalGrants.add(userPersonalGrant)
        if (this != userPersonalGrant.menuNavigation)
            userPersonalGrant.setBy(this)
    }

    fun expire() {
        val userPersonalGrants: List<UserPersonalGrant> = this.userPersonalGrants
        userPersonalGrants.forEach {
            it.expire()
        }
        status = false
    }
}

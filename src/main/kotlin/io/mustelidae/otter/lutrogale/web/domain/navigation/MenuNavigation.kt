package io.mustelidae.otter.lutrogale.web.domain.navigation

import io.mustelidae.otter.lutrogale.common.Audit
import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.grant.UserPersonalGrant
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
class MenuNavigation(
    @Column(nullable = false, length = 250)
    var name: String,
    @Enumerated(STRING)
    var type: Constant.NavigationType,
    var uriBlock: String,
    @Enumerated(STRING)
    var methodType: RequestMethod,
    var treeId: String,
    var parentTreeId: String,
) : Audit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @ManyToOne
    @JoinColumn(name = "projectId")
    var project: Project? = null
        private set

    @ManyToOne
    @JoinColumn(name = "parentId")
    var parentMenuNavigation: MenuNavigation? = null
        private set

    @OneToMany(mappedBy = "parentMenuNavigation", fetch = LAZY, cascade = [ALL])
    var menuNavigations: MutableList<MenuNavigation> = ArrayList()
        private set

    @OneToMany(mappedBy = "menuNavigation", fetch = LAZY, cascade = [ALL])
    var authorityNavigationUnits: MutableList<AuthorityNavigationUnit> = arrayListOf()
        private set

    @OneToMany(mappedBy = "menuNavigation", fetch = LAZY, cascade = [ALL])
    var userPersonalGrants: MutableList<UserPersonalGrant> = arrayListOf()
        private set

    var status = true
        private set

    fun setBy(parentMenuNavigation: MenuNavigation) {
        this.parentMenuNavigation = parentMenuNavigation
        if (!parentMenuNavigation.menuNavigations.contains(this)) {
            parentMenuNavigation.addBy(this)
        }
    }

    fun setBy(project: Project) {
        this.project = project
        if (!project.menuNavigations.contains(this)) {
            project.addBy(this)
        }
    }

    fun addBy(authorityNavigationUnit: AuthorityNavigationUnit) {
        authorityNavigationUnits.add(authorityNavigationUnit)
        if (authorityNavigationUnit.menuNavigation != this) {
            authorityNavigationUnit.setBy(this)
        }
    }

    fun addBy(menuNavigation: MenuNavigation) {
        menuNavigations.add(menuNavigation)
        if (this != menuNavigation.parentMenuNavigation) {
            menuNavigation.setBy(this)
        }
    }

    fun addBy(userPersonalGrant: UserPersonalGrant) {
        userPersonalGrants.add(userPersonalGrant)
        if (this != userPersonalGrant.menuNavigation) {
            userPersonalGrant.setBy(this)
        }
    }

    fun expire() {
        val userPersonalGrants: List<UserPersonalGrant> = this.userPersonalGrants
        userPersonalGrants.forEach {
            it.expire()
        }
        status = false
    }

    companion object {
        fun root(): MenuNavigation {
            return MenuNavigation(
                name = "root",
                type = Constant.NavigationType.CATEGORY,
                uriBlock = "/",
                methodType = RequestMethod.GET,
                treeId = "1",
                parentTreeId = "#",
            )
        }
    }
}

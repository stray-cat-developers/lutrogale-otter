package io.mustelidae.otter.lutrogale.web.domain.grant

import io.mustelidae.smoothcoatedotter.api.common.Audit
import io.mustelidae.smoothcoatedotter.web.commons.exception.ApplicationException
import io.mustelidae.smoothcoatedotter.web.commons.exception.ProcessErr
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigation
import io.mustelidae.smoothcoatedotter.web.domain.project.Project
import org.hibernate.annotations.Where
import java.util.function.Consumer
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

/**
 * Created by HanJaehyun on 2016. 9. 21..
 * 권한 정의 그룹
 */
@Entity
class AuthorityDefinition(
    @Column(nullable = false, length = 50)
    val name: String
) : Audit() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    @ManyToOne
    @JoinColumn(name = "projectId")
    var project: Project? = null
        protected set

    var status = true
        protected set

    @Where(clause = "status = true")
    @OneToMany(
        mappedBy = "authorityDefinition",
        fetch = LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH]
    )
    var authorityNavigationUnits: MutableList<AuthorityNavigationUnit> = arrayListOf()
        protected set

    @OneToMany(mappedBy = "authorityDefinition", fetch = LAZY)
    @Where(clause = "status = true")
    var userAuthorityGrants: MutableList<UserAuthorityGrant> = arrayListOf()
        protected set

    fun expire() {
        status = false
        authorityNavigationUnits.forEach { it.expire() }
        userAuthorityGrants.forEach(Consumer { grant: UserAuthorityGrant -> grant.expire() })
    }

    val menuNavigations: List<MenuNavigation>
        get() = authorityNavigationUnits.map { it.menuNavigation!! }

    fun addBy(authorityNavigationUnit: AuthorityNavigationUnit) {
        authorityNavigationUnits.add(authorityNavigationUnit)
        if (authorityNavigationUnit.authorityDefinition != this)
            authorityNavigationUnit.setBy(this)
    }

    fun addBy(menuNavigation: MenuNavigation) {
        if (menuNavigation.id == null)
            throw ApplicationException(ProcessErr.WRONG_DEVELOP_PROCESS)

        require(!menuNavigations.contains(menuNavigation)) { "이미 해당 메뉴는 등록되어 있습니다." }

        val unit = AuthorityNavigationUnit()

        unit.setBy(menuNavigation)
        unit.setBy(this)
    }

    fun removeBy(menuNavigation: MenuNavigation?) {
        val target = this.authorityNavigationUnits.find { it.menuNavigation!!.id!! == menuNavigation!!.id }!!
        target.expire()
    }

    fun addBy(userAuthorityGrant: UserAuthorityGrant) {
        this.userAuthorityGrants.add(userAuthorityGrant)
        if (userAuthorityGrant.authorityDefinition != this)
            userAuthorityGrant.setBy(this)
    }

    fun setBy(project: Project) {
        this.project = project
        if (project.authorityDefinitions.contains(this).not())
            project.addBy(this)
    }
}

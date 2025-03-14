package io.mustelidae.otter.lutrogale.web.domain.authority

import io.mustelidae.otter.lutrogale.common.Audit
import io.mustelidae.otter.lutrogale.web.domain.grant.UserAuthorityGrant
import io.mustelidae.otter.lutrogale.web.domain.navigation.AuthorityNavigationUnit
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.SQLRestriction
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode

/**
 * Created by HanJaehyun on 2016. 9. 21..
 * 권한 정의 그룹
 */
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
class AuthorityDefinition(
    @Column(nullable = false, length = 50)
    val name: String,
) : Audit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @ManyToOne
    @JoinColumn(name = "projectId")
    var project: Project? = null
        private set

    var status = true
        private set

    @OneToMany(mappedBy = "authorityDefinition", fetch = LAZY)
    @SQLRestriction("status = true")
    var authorityNavigationUnits: MutableList<AuthorityNavigationUnit> = arrayListOf()
        private set

    @OneToMany(
        mappedBy = "authorityDefinition",
        fetch = LAZY,
        cascade = [CascadeType.ALL],
    )
    @SQLRestriction("status = true")
    var userAuthorityGrants: MutableList<UserAuthorityGrant> = arrayListOf()
        private set

    fun expire() {
        status = false
        authorityNavigationUnits.forEach { it.expire() }
        userAuthorityGrants.forEach { it.expire() }
    }

    val menuNavigations: List<MenuNavigation>
        get() = authorityNavigationUnits.map { it.menuNavigation!! }

    fun addBy(authorityNavigationUnit: AuthorityNavigationUnit) {
        authorityNavigationUnits.add(authorityNavigationUnit)
        if (authorityNavigationUnit.authorityDefinition != this) {
            authorityNavigationUnit.setBy(this)
        }
    }

    fun addBy(userAuthorityGrant: UserAuthorityGrant) {
        this.userAuthorityGrants.add(userAuthorityGrant)
        if (userAuthorityGrant.authorityDefinition != this) {
            userAuthorityGrant.setBy(this)
        }
    }

    fun setBy(project: Project) {
        this.project = project
        if (project.authorityDefinitions.contains(this).not()) {
            project.addBy(this)
        }
    }
}

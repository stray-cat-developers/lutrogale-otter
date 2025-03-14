package io.mustelidae.otter.lutrogale.web.domain.user

import io.mustelidae.otter.lutrogale.common.Audit
import io.mustelidae.otter.lutrogale.config.InvalidArgumentException
import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityDefinition
import io.mustelidae.otter.lutrogale.web.domain.grant.UserAuthorityGrant
import io.mustelidae.otter.lutrogale.web.domain.grant.UserPersonalGrant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.hibernate.annotations.SQLRestriction
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode

/**
 * 어드민의 권한을 체크해야 하는 운영자의 정보
 */
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
class User(
    @Column(nullable = false, length = 30)
    var name: String,
    @Column(unique = true, nullable = false, length = 50)
    val email: String,
) : Audit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @Column(length = 50)
    var department: String? = null

    var isPrivacy = false

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    var status: Status = Status.WAIT

    @SQLRestriction("status = true")
    @OneToMany(mappedBy = "user", fetch = LAZY, cascade = [ALL])
    var userAuthorityGrants: MutableList<UserAuthorityGrant> = arrayListOf()
        private set

    val authorityDefinitions: List<AuthorityDefinition>
        get() = userAuthorityGrants.map { it.authorityDefinition!! }

    fun addBy(userAuthorityGrant: UserAuthorityGrant) {
        if (this.userAuthorityGrants.contains(userAuthorityGrant)) {
            throw InvalidArgumentException("이미 해당 권한 그룹은 허용되어 있습니다.")
        }

        userAuthorityGrants.add(userAuthorityGrant)
        if (userAuthorityGrant.user != this) {
            userAuthorityGrant.setBy(this)
        }
    }

    @SQLRestriction("status = true")
    @OneToMany(mappedBy = "user", fetch = LAZY, cascade = [ALL])
    var userPersonalGrants: MutableList<UserPersonalGrant> = arrayListOf()
        private set

    val menuNavigations: List<MenuNavigation>
        get() = userPersonalGrants.map { it.menuNavigation!! }

    fun addBy(menuNavigation: MenuNavigation) {
        if (this.menuNavigations.contains(menuNavigation)) {
            throw IllegalStateException("이미 해당 권한 그룹은 허용되어 있습니다.")
        }

        val userPersonalGrant = UserPersonalGrant()
        userPersonalGrant.setBy(this)
        userPersonalGrant.setBy(menuNavigation)

        addBy(userPersonalGrant)
    }

    fun addBy(userPersonalGrant: UserPersonalGrant) {
        this.userPersonalGrants.add(userPersonalGrant)
        if (userPersonalGrant.user != this) {
            userPersonalGrant.setBy(this)
        }
    }

    fun expire() {
        status = Status.EXPIRE
    }

    fun getProjects(): List<Project> {
        return this.authorityDefinitions.map { it.project!! }
    }

    enum class Status {
        /* 허가 */
        ALLOW,

        /* 불가 */
        REJECT,

        /* 대기 */
        WAIT,

        /* 만료 */
        EXPIRE,
    }
}

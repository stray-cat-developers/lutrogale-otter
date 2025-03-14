package io.mustelidae.otter.lutrogale.web.domain.project

import io.mustelidae.otter.lutrogale.common.Audit
import io.mustelidae.otter.lutrogale.utils.Crypto
import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityDefinition
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(indexes = [Index(name = "IDX_APIKEY", columnList = "apiKey", unique = true)])
class Project(
    @Column(nullable = false, length = 50)
    var name: String,
    @Column(length = 500)
    var description: String? = null,
    @Column(length = 100)
    var apiKey: String,
) : Audit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    var status = true
        private set

    @SQLRestriction("status = true")
    @OneToMany(
        mappedBy = "project",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH],
    )
    var menuNavigations: MutableList<MenuNavigation> = arrayListOf()
        private set

    @SQLRestriction("status = true")
    @OneToMany(
        mappedBy = "project",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH],
    )
    var authorityDefinitions: MutableList<AuthorityDefinition> = arrayListOf()
        private set

    fun expire() {
        status = false
    }

    fun addBy(menuNavigation: MenuNavigation) {
        menuNavigations.add(menuNavigation)
        if (this != menuNavigation.project) {
            menuNavigation.setBy(this)
        }
    }

    fun addBy(authorityDefinition: AuthorityDefinition) {
        authorityDefinitions.add(authorityDefinition)
        if (authorityDefinition.project != this) {
            authorityDefinition.setBy(this)
        }
    }

    companion object {
        fun of(name: String, description: String?): Project {
            val time: Long = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            return Project(
                name,
                description,
                Crypto.sha256(name + time),
            )
        }
    }
}

package io.mustelidae.otter.lutrogale.web.domain.project

import io.mustelidae.otter.lutrogale.common.Audit
import io.mustelidae.otter.lutrogale.utils.Crypto
import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityDefinition
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import org.hibernate.envers.Audited
import org.hibernate.envers.NotAudited
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
    @NotAudited
    @Column(length = 100)
    var apiKey: String,
    @NotAudited
    @Enumerated(EnumType.STRING)
    @Column(name = "list_structure", length = 10)
    var listStructure: MenuNavigation.ListStructure,
) : Audit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    var status = true
        protected set

    @Column(name = "spec_type", length = 50)
    var specType: SpecType? = null
        protected set

    @Column(name = "migration_url", length = 500)
    var migrationUrl: String? = null
        protected set

    @Column(name = "sync_enabled")
    var syncEnabled: Boolean = false
        protected set

    @SQLRestriction("status = true")
    @OneToMany(
        mappedBy = "project",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH],
    )
    var menuNavigations: MutableList<MenuNavigation> = arrayListOf()
        protected set

    @SQLRestriction("status = true")
    @OneToMany(
        mappedBy = "project",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH],
    )
    var authorityDefinitions: MutableList<AuthorityDefinition> = arrayListOf()
        protected set

    fun expire() {
        status = false
    }

    fun setSync(
        spec: SpecType,
        url: String,
    ) {
        specType = spec
        migrationUrl = url
        syncEnabled = true
    }

    fun removeSync() {
        specType = null
        migrationUrl = null
        syncEnabled = false
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

    enum class SpecType {
        OPENAPI_JSON,
        OPENAPI_YAML,
        GRAPHQL,
    }

    companion object {
        fun of(
            name: String,
            description: String?,
            listStructure: MenuNavigation.ListStructure,
        ): Project {
            val time: Long =
                LocalDateTime
                    .now()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            return Project(
                name,
                description,
                Crypto.sha256(name + time),
                listStructure,
            )
        }
    }
}

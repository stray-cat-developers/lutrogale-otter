package io.mustelidae.otter.lutrogale.web.domain.project

import io.mustelidae.smoothcoatedotter.api.common.Audit
import io.mustelidae.smoothcoatedotter.web.commons.utils.EncryptUtil
import io.mustelidae.smoothcoatedotter.web.domain.grant.AuthorityDefinition
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigation
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Entity
@Table(indexes = [Index(name = "IDX_APIKEY", columnList = "apiKey", unique = true)])
class Project(
    @Column(nullable = false, length = 50)
    var name: String,
    @Column(length = 500)
    var description: String? = null,
    @Column(length = 100)
    var apiKey: String
) : Audit() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var status = true
        protected set

    @Where(clause = "status = true")
    @OneToMany(
        mappedBy = "project",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH]
    )
    var menuNavigations: MutableList<MenuNavigation> = arrayListOf()
        protected set

    @Where(clause = "status = true")
    @OneToMany(
        mappedBy = "project",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH]
    )
    var authorityDefinitions: MutableList<AuthorityDefinition> = arrayListOf()
        protected set

    fun expire() {
        status = false
    }

    fun addBy(menuNavigation: MenuNavigation) {
        menuNavigations.add(menuNavigation)
        if (this != menuNavigation.project)
            menuNavigation.setBy(this)
    }

    fun addBy(authorityDefinition: AuthorityDefinition) {
        authorityDefinitions.add(authorityDefinition)
        if (authorityDefinition.project != this)
            authorityDefinition.setBy(this)
    }

    companion object {
        fun of(name: String, description: String?): Project {
            val time: Long = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            return Project(
                name,
                description,
                EncryptUtil.sha256(name + time)
            )
        }
    }
}

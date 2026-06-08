package io.mustelidae.otter.lutrogale.web.domain.admin

import io.mustelidae.otter.lutrogale.common.Audit
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.SQLRestriction
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
class Admin(
    @Column(unique = true, nullable = false)
    val email: String,
    val name: String,
    var description: String? = null,
    var img: String? = null,
) : Audit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @Column(nullable = false)
    var pw: String? = null
        private set

    var status = true
        private set

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    var role: AdminRole = AdminRole.REGULAR
        private set

    @ManyToOne
    @JoinColumn(name = "parentId")
    var parentAdmin: Admin? = null
        private set

    @SQLRestriction("status = true")
    @OneToMany(
        mappedBy = "parentAdmin",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH],
    )
    val admins: MutableList<Admin> = ArrayList()

    fun setBy(parentAdmin: Admin) {
        this.parentAdmin = parentAdmin
        if (!parentAdmin.admins.contains(this)) parentAdmin.addBy(this)
    }

    fun addBy(admin: Admin) {
        admins.add(admin)
        if (this != admin.parentAdmin) admin.setBy(this)
    }

    fun setPassword(pw: String) {
        this.pw = passwordEncoder.encode(pw)
    }

    fun matchesPassword(raw: String): Boolean {
        val encrypted = this.pw ?: return false
        return passwordEncoder.matches(raw, encrypted)
    }

    fun expire() {
        status = false
    }

    companion object {
        private val passwordEncoder = BCryptPasswordEncoder(10)

        fun of(email: String, pw: String, name: String, description: String?, img: String?, role: AdminRole = AdminRole.REGULAR): Admin {
            return Admin(email, name, description, img).apply {
                setPassword(pw)
                this.role = role
            }
        }
    }
}

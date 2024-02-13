package io.mustelidae.otter.lutrogale.web.domain.admin

import io.mustelidae.otter.lutrogale.utils.Crypto
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.util.ArrayList

/**
 * Created by HanJaehyun on 2016. 9. 20..
 */
@Entity
class Admin(
    @Column(unique = true, nullable = false)
    val email: String,
    val name: String,
    var description: String? = null,
    var img: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @Column(nullable = false)
    var pw: String? = null
        private set

    var status = false

    @ManyToOne
    @JoinColumn(name = "parentId")
    private var parentAdmin: Admin? = null

    @OneToMany(
        mappedBy = "parentAdmin",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH],
    )
    private val admins: MutableList<Admin> = ArrayList()
    fun setBy(parentAdmin: Admin) {
        this.parentAdmin = parentAdmin
        if (!parentAdmin.admins.contains(this)) parentAdmin.addBy(this)
    }

    fun addBy(admin: Admin) {
        admins.add(admin)
        if (this != admin.parentAdmin) admin.setBy(this)
    }

    fun setPassword(pw: String) {
        this.pw = Crypto.sha256(pw)
    }

    companion object {
        fun of(email: String, pw: String, name: String, description: String?, img: String?): Admin {
            return Admin(
                email,
                name,
                description,
                img,
            ).apply {
                setPassword(pw)
            }
        }
    }
}

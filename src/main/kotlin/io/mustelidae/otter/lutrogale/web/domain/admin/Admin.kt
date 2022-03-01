package io.mustelidae.otter.lutrogale.web.domain.admin

import io.mustelidae.smoothcoatedotter.web.commons.utils.EncryptUtil
import java.util.ArrayList
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

/**
 * Created by HanJaehyun on 2016. 9. 20..
 */
@Entity
class Admin(
    @Column(unique = true, nullable = false)
    val email: String,
    val name: String,
    var description: String? = null,
    var img: String? = null
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    @Column(nullable = false)
    var pw: String? = null
        protected set


    var status = false

    @ManyToOne
    @JoinColumn(name = "parentId")
    private var parentAdmin: Admin? = null

    @OneToMany(
        mappedBy = "parentAdmin",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH]
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
        this.pw = EncryptUtil.sha256(pw)
    }

    companion object {
        fun of(email: String, pw: String, name: String, description: String?, img: String?): Admin {
            return Admin(
                email,
                name,
                description,
                img
            ).apply {
                setPassword(pw)
            }
        }
    }
}

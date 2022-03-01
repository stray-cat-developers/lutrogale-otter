package io.mustelidae.otter.lutrogale.web.domain.admin.repository

import io.mustelidae.smoothcoatedotter.web.domain.admin.Admin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by HanJaehyun on 2016. 9. 20..
 */
@Repository
interface AdminRepository : JpaRepository<Admin, Long> {
    fun findByEmailAndPw(email: String, pw: String): Admin?
}

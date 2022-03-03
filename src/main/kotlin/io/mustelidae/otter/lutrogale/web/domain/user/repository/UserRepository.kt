package io.mustelidae.otter.lutrogale.web.domain.user.repository

import io.mustelidae.otter.lutrogale.web.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun countAllByStatusIn(statuses: List<User.Status>): Int
    fun findByEmail(email: String): User?
}

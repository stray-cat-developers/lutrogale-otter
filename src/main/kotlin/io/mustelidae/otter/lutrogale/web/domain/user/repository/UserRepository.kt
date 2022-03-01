package io.mustelidae.otter.lutrogale.web.domain.user.repository

import io.mustelidae.otter.lutrogale.web.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun countAllByStatusIn(statuses: List<User.Status>): Int

    // FIXME: DSL로 변경하자.
    @Query(
        "SELECT DISTINCT u " +
            "FROM AuthorityDefinition ad " +
            "JOIN ad.project pj " +
            "JOIN ad.userAuthorityGrants uag " +
            "JOIN uag.user u " +
            "WHERE pj.id = :projectId " +
            "AND u.status = 'allow' " +
            "AND ad.status = true " +
            "AND uag.status = true "
    )
    fun findAllByJoinedProjectUsers(@Param("projectId") projectId: Long): List<User>

    fun findByEmail(email: String): User
}

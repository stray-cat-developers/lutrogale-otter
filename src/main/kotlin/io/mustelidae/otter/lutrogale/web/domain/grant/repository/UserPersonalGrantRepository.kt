package io.mustelidae.otter.lutrogale.web.domain.grant.repository

import io.mustelidae.otter.lutrogale.web.domain.grant.UserPersonalGrant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

/**
 * Created by seooseok on 2016. 10. 5..
 */
interface UserPersonalGrantRepository : JpaRepository<UserPersonalGrant?, Long?> {
    @Query("SELECT DISTINCT upg FROM UserPersonalGrant upg JOIN FETCH upg.user u JOIN FETCH upg.menuNavigation mn JOIN FETCH mn.project p WHERE upg.status = true AND upg.user.id = :userId")
    fun findByStatusTrueAndUserId(@Param("userId") userId: Long): List<UserPersonalGrant?>?
}

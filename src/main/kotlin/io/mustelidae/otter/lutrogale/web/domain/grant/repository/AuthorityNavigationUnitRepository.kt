package io.mustelidae.otter.lutrogale.web.domain.grant.repository

import io.mustelidae.smoothcoatedotter.web.domain.grant.AuthorityNavigationUnit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Created by seooseok on 2016. 9. 30..
 */
@Repository
interface AuthorityNavigationUnitRepository : JpaRepository<AuthorityNavigationUnit, Long> {
    fun findByStatusTrueAndAuthorityDefinitionIdAndMenuNavigationId(
        authorityDefinitionId: Long,
        menuNavigationId: Long
    ): AuthorityNavigationUnit?

    fun findByStatusTrueAndAuthorityDefinitionIdAndMenuNavigationIdIn(
        authorityDefinitionId: Long,
        menuNavigationIdGroup: List<Long>
    ): List<AuthorityNavigationUnit>?

    fun findByStatusTrueAndMenuNavigationId(menuNavigationId: Long): List<AuthorityNavigationUnit>

    @Query(
        "SELECT DISTINCT an " +
            " FROM AuthorityNavigationUnit an JOIN FETCH an.authorityDefinition ad JOIN FETCH an.menuNavigation mn " +
            "WHERE an.status = true " +
            "AND an.authorityDefinition.id = :authorityDefinitionId"
    )
    fun findByStatusTrueAndAuthorityDefinitionId(@Param("authorityDefinitionId") authorityDefinitionId: Long): List<AuthorityNavigationUnit>?

    @Query(
        "SELECT DISTINCT an " +
            "FROM AuthorityNavigationUnit an JOIN FETCH an.authorityDefinition ad JOIN FETCH an.menuNavigation mn " +
            "WHERE an.status = true " +
            "AND an.authorityDefinition.id in (:authorityDefinitionIdGroup)"
    )
    fun findByStatusTrueAndAuthorityDefinitionIdGroup(@Param("authorityDefinitionIdGroup") authorityDefinitionIdGroup: List<Long>): List<AuthorityNavigationUnit>?
}

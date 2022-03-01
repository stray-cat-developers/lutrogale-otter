package io.mustelidae.otter.lutrogale.web.domain.navigation.repository

import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by HanJaehyun on 2016. 9. 22..
 */
@Repository
interface MenuNavigationRepository : JpaRepository<MenuNavigation, Long> {
    fun findByProjectIdAndId(projectId: Long, nodeId: Long): MenuNavigation?
    fun findByProjectIdAndTreeId(projectId: Long, treeId: String): MenuNavigation?
    fun findByStatusTrueAndProjectId(projectId: Long): List<MenuNavigation>?
    fun findByStatusTrueAndProjectIdAndIdIn(projectId: Long, menuNavigationIds: List<Long>): List<MenuNavigation>
    fun findByIdIn(menuNavigationIds: List<Long>): List<MenuNavigation>
}

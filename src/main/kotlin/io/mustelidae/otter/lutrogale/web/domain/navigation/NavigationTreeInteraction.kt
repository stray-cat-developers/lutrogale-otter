package io.mustelidae.otter.lutrogale.web.domain.navigation

import io.mustelidae.otter.lutrogale.config.DataNotFindException
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.MenuTreeResources
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.MenuTreeResources.Request.Branch
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.NavigationResources.Reply.ReplyOfMenuNavigation.Companion.from
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.AuthorityNavigationUnitRepository
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by HanJaehyun on 2016. 9. 22..
 */
@Service
@Transactional
class NavigationTreeInteraction(
    private val menuNavigationRepository: MenuNavigationRepository,
    private val authorityNavigationUnitRepository: AuthorityNavigationUnitRepository,
    private val menuNavigationInteraction: MenuNavigationInteraction,
    private val menuNavigationFinder: MenuNavigationFinder,
    private val projectFinder: ProjectFinder,
) {

    fun createBranch(projectId: Long, branch: Branch): Long {
        val project = projectFinder.findByLive(projectId)
        val parentMenuNavigation =
            menuNavigationRepository.findByProjectIdAndTreeId(projectId, branch.parentTreeId)

        val menuNavigation = MenuNavigation(
            branch.name,
            branch.type,
            branch.getRefineUriBlock(),
            branch.methodType,
            branch.treeId,
            branch.parentTreeId,
        ).apply {
            setBy(project)
        }

        parentMenuNavigation?.let {
            menuNavigation.setBy(it)
        }

        return menuNavigationRepository.save(menuNavigation).id!!
    }

    fun getTreeBranches(projectId: Long): List<MenuTreeResources.Reply.TreeBranch> {
        val treeBranches: MutableList<MenuTreeResources.Reply.TreeBranch> = ArrayList()
        val project = projectFinder.findByLive(projectId)
        val menuNavigations: List<MenuNavigation> = project.menuNavigations
        if (menuNavigations.isEmpty()) {
            throw DataNotFindException("메뉴네비게이션 정보가 없습니다.")
        }

        for (menuNavigation in menuNavigations) {
            treeBranches.add(this.getTreeBranch(menuNavigation))
        }
        return treeBranches
    }

    fun getTreeBranch(projectId: Long, menuNavigationId: Long): MenuTreeResources.Reply.TreeBranch {
        val navigation = menuNavigationFinder.findByMenuNavigationId(projectId, menuNavigationId)
        return this.getTreeBranch(navigation)
    }

    private fun getTreeBranch(menuNavigation: MenuNavigation): MenuTreeResources.Reply.TreeBranch {
        val fullUrl = menuNavigationInteraction.getFullUrl(menuNavigation)
        val menuNavigationResource = from(menuNavigation, fullUrl)
        return MenuTreeResources.Reply.TreeBranch.of(menuNavigation.treeId, menuNavigation.parentTreeId, menuNavigationResource)
    }

    fun moveBranch(projectId: Long, nodeId: Long, newParentId: String?): Boolean {
        val navigation = menuNavigationFinder.findByMenuNavigationId(projectId, nodeId)
        navigation.parentTreeId = newParentId!!
        val parentMenuNavigation = menuNavigationFinder.findByTreeId(projectId, newParentId)
        navigation.setBy(parentMenuNavigation)
        return true
    }

    fun removeBranch(projectId: Long, id: Long): Boolean {
        val menuNavigation = menuNavigationFinder.findByMenuNavigationId(projectId, id)
        menuNavigation.expire()

        val authorityNavigationUnits: List<AuthorityNavigationUnit> =
            authorityNavigationUnitRepository.findByStatusTrueAndMenuNavigationId(menuNavigation.id!!)
        if (authorityNavigationUnits.isNotEmpty()) {
            for (authorityNavigationUnit in authorityNavigationUnits) {
                authorityNavigationUnit.expire()
            }
            authorityNavigationUnitRepository.saveAll(authorityNavigationUnits)
        }
        menuNavigationRepository.save(menuNavigation)
        val childMenuNavigationGroup: List<MenuNavigation> = menuNavigation.menuNavigations
        if (childMenuNavigationGroup.isNotEmpty()) {
            for (navigation in childMenuNavigationGroup) {
                removeBranch(projectId, navigation.id!!)
            }
        }
        return true
    }
}

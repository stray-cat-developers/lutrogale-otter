package io.mustelidae.otter.lutrogale.web.domain.navigation

import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.HumanErr
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.MenuNavigationResource.Companion.from
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.MenuTreeResources.Request.Branch
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.TreeBranchResource
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.AuthorityNavigationUnitRepository
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.ArrayList
import java.util.Optional

/**
 * Created by HanJaehyun on 2016. 9. 22..
 */
@Service
@Transactional
class NavigationTree(
    private val menuNavigationRepository: MenuNavigationRepository,
    private val authorityNavigationUnitRepository: AuthorityNavigationUnitRepository,
    private val menuNavigationManager: MenuNavigationManager,
    private val projectFinder: ProjectFinder
) {

    fun createBranch(projectId: Long, branch: Branch): Long {
        val project = projectFinder.findByLive(projectId)
        val parentMenuNavigation =
            menuNavigationRepository.findByProjectIdAndTreeId(projectId, branch.parentTreeId)

        val menuNavigation = MenuNavigation(
            branch.name,
            branch.type,
            branch.uriBlock,
            branch.methodType,
            branch.treeId,
            branch.parentTreeId
        ).apply {
            setBy(project)
        }

        parentMenuNavigation?.let {
            menuNavigation.setBy(it)
        }

        return menuNavigationRepository.save(menuNavigation).id!!
    }

    fun getTreeBranches(projectId: Long): List<TreeBranchResource> {
        val treeBranchResources: MutableList<TreeBranchResource> = ArrayList()
        val project = projectFinder.findByLive(projectId)
        val menuNavigations: List<MenuNavigation> = project.menuNavigations
        if (menuNavigations.isEmpty())
            throw ApplicationException(HumanErr.IS_EMPTY)

        for (menuNavigation in menuNavigations) {
            treeBranchResources.add(this.getTreeBranch(menuNavigation))
        }
        return treeBranchResources
    }

    fun getTreeBranch(projectId: Long, menuNavigationId: Long): TreeBranchResource {
        val navigation = menuNavigationManager.findBy(projectId, menuNavigationId)
        return this.getTreeBranch(navigation)
    }

    private fun getTreeBranch(menuNavigation: MenuNavigation): TreeBranchResource {
        val fullUrl = menuNavigationManager.getFullUrl(menuNavigation)
        val menuNavigationResource = from(menuNavigation, fullUrl)
        return TreeBranchResource.of(menuNavigation.treeId, menuNavigation.parentTreeId, menuNavigationResource)
    }

    fun moveBranch(projectId: Long, nodeId: Long, newParentId: String?): Boolean {
        val menuNavigation = Optional.ofNullable(menuNavigationRepository.findByProjectIdAndId(projectId, nodeId))
        if (!menuNavigation.isPresent) throw ApplicationException(HumanErr.IS_EMPTY)
        val navigation = menuNavigation.get()
        navigation.parentTreeId = newParentId!!
        val parentMenuNavigation = menuNavigationRepository.findByProjectIdAndTreeId(projectId, newParentId)
        navigation.setBy(parentMenuNavigation!!)
        return true
    }

    fun removeBranch(projectId: Long, id: Long): Boolean {
        val menuNavigation = menuNavigationRepository.findByProjectIdAndId(projectId, id)
            ?: throw ApplicationException(HumanErr.IS_EMPTY)

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

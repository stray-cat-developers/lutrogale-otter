package io.mustelidae.otter.lutrogale.web.domain.grant

import io.mustelidae.smoothcoatedotter.web.commons.exception.ApplicationException
import io.mustelidae.smoothcoatedotter.web.commons.exception.HumanErr
import io.mustelidae.smoothcoatedotter.web.domain.grant.repository.AuthorityNavigationUnitRepository
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigation
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigationManager
import io.mustelidae.smoothcoatedotter.web.domain.navigation.api.MenuNavigationResource
import io.mustelidae.smoothcoatedotter.web.domain.navigation.api.MenuNavigationResource.Companion.of
import io.mustelidae.smoothcoatedotter.web.domain.navigation.api.TreeBranchResource
import io.mustelidae.smoothcoatedotter.web.domain.project.ProjectManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Consumer

@Service
@Transactional
class AuthorityBundle(
    private val projectManager: ProjectManager,
    private val authorityDefinitionManager: AuthorityDefinitionManager,
    private val authorityNavigationUnitRepository: AuthorityNavigationUnitRepository,
    private val menuNavigationManager: MenuNavigationManager,
) {

    fun hasMenuNavigations(authorityDefinitionId:Long): List<MenuNavigation>{
        val authorityNavigationUnits =
            authorityNavigationUnitRepository.findByStatusTrueAndAuthorityDefinitionId(authorityDefinitionId)?: emptyList()

        return authorityNavigationUnits.map { it.menuNavigation!! }
    }

    fun addBy(authorityDefinitionId: Long, navigationIdGroup: List<Long>) {
        val authorityDefinition = authorityDefinitionManager.findByLive(authorityDefinitionId)
        val projectId = authorityDefinition.project!!.id!!
        val menuNavigations = menuNavigationManager.findByLive(projectId, navigationIdGroup)
        val targetMenuNavigations: MutableList<MenuNavigation> = arrayListOf()

        for (menuNavigation in menuNavigations) {
            putInParentMenuNavigation(targetMenuNavigations, menuNavigation)
        }

        val currentMenuNavigationGroup = authorityDefinition.menuNavigations
        targetMenuNavigations.forEach(Consumer { menuNavigation: MenuNavigation ->
            if (!currentMenuNavigationGroup.contains(
                    menuNavigation
                )
            ) authorityDefinition.addBy(menuNavigation)
        })
    }

    fun createBundle(projectId: Long, name: String?, menuNavigationIdGroup: List<Long>): Long {
        val project = projectManager.findBy(projectId)
        val authorityDefinition = authorityDefinitionManager.createBy(project, name!!)
        this.addBy(authorityDefinition.id!!, menuNavigationIdGroup)
        return authorityDefinition.id!!
    }

    fun getBundles(projectId: Long): List<AuthorityDefinition> {
        return authorityDefinitionManager.findListBy(projectId)
    }

    fun lookInBundle(authorityDefinitionId: Long): List<MenuNavigationResource> {
        val menuNavigationResources: MutableList<MenuNavigationResource> = ArrayList()
        val currentMenuNavigationGroup: List<MenuNavigation> = this.hasMenuNavigations(authorityDefinitionId)
        for (menuNavigation in currentMenuNavigationGroup) {
            val fullUrl = menuNavigationManager.getFullUrl(menuNavigation)
            menuNavigationResources.add(of(menuNavigation, fullUrl))
        }
        return menuNavigationResources
    }

    fun lookInBundleForTreeFormat(authorityDefinitionId: Long): List<TreeBranchResource> {
        val treeBranchResources: MutableList<TreeBranchResource> = java.util.ArrayList()
        val currentMenuNavigationGroup: List<MenuNavigation> = this.hasMenuNavigations(authorityDefinitionId)
        for (menuNavigation in currentMenuNavigationGroup) {
            val fullUrl = menuNavigationManager.getFullUrl(menuNavigation)
            val menuNavigationResource = of(menuNavigation, fullUrl)
            treeBranchResources.add(
                TreeBranchResource.of(
                    menuNavigation.treeId,
                    menuNavigation.parentTreeId,
                    menuNavigationResource
                )
            )
        }
        return treeBranchResources
    }

    fun modifyBundlesNavigation(projectId: Long, authorityDefinitionId: Long, menuNavigationId: Long) {
        val authorityDefinition = authorityDefinitionManager.findBy(authorityDefinitionId)
        val menuNavigation = menuNavigationManager.findBy(projectId, menuNavigationId)
        authorityDefinition.addBy(menuNavigation)
    }


    fun expireBy(projectId: Long, authorityDefinitionId: Long) {
        val authorityDefinition = authorityDefinitionManager.findByLive(authorityDefinitionId)
        if (authorityDefinition.project!!.id != projectId) throw ApplicationException(HumanErr.INVALID_INCLUDE)
        authorityDefinition.expire()
    }

    fun expireAuthorityNavigation(projectId: Long, authorityDefinitionId: Long, menuNavigationIdGroup: List<Long>) {
        val authorityDefinition = authorityDefinitionManager.findByLive(authorityDefinitionId)
        if (authorityDefinition.project!!.id != projectId) throw ApplicationException(HumanErr.INVALID_INCLUDE)
        val menuNavigations = menuNavigationManager.findByLive(projectId, menuNavigationIdGroup)
        menuNavigations.forEach(Consumer { menuNavigation: MenuNavigation? ->
            authorityDefinition.removeBy(
                menuNavigation
            )
        })
    }

    private fun putInParentMenuNavigation(
        menuNavigations: MutableList<MenuNavigation>,
        menuNavigation: MenuNavigation
    ) {
        menuNavigations.add(menuNavigation)
        val parentMenuNavigation = menuNavigation.parentMenuNavigation
        if (parentMenuNavigation != null && !menuNavigations.contains(parentMenuNavigation)) {
            putInParentMenuNavigation(menuNavigations, parentMenuNavigation)
        }
    }

}
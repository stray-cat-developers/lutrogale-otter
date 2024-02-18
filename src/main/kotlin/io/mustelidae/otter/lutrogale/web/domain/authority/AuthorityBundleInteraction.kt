package io.mustelidae.otter.lutrogale.web.domain.authority

import io.mustelidae.otter.lutrogale.web.domain.authority.repository.AuthorityDefinitionRepository
import io.mustelidae.otter.lutrogale.web.domain.navigation.AuthorityNavigationUnit
import io.mustelidae.otter.lutrogale.web.domain.navigation.DefinitionAndNavigationFinder
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationFinder
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationInteraction
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.MenuTreeResources
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.NavigationResources.Reply.ReplyOfMenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.NavigationResources.Reply.ReplyOfMenuNavigation.Companion.from
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.AuthorityNavigationUnitRepository
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthorityBundleInteraction(
    private val projectFinder: ProjectFinder,
    private val authorityDefinitionInteraction: AuthorityDefinitionInteraction,
    private val authorityNavigationUnitRepository: AuthorityNavigationUnitRepository,
    private val definitionAndNavigationFinder: DefinitionAndNavigationFinder,
    private val menuNavigationInteraction: MenuNavigationInteraction,
    private val authorityDefinitionRepository: AuthorityDefinitionRepository,
    private val authorityDefinitionFinder: AuthorityDefinitionFinder,
    private val menuNavigationFinder: MenuNavigationFinder,
) {

    fun hasMenuNavigations(authorityDefinitionId: Long): List<MenuNavigation> {
        val authorityNavigationUnits =
            authorityNavigationUnitRepository.findByStatusTrueAndAuthorityDefinitionId(authorityDefinitionId) ?: emptyList()

        return authorityNavigationUnits.map { it.menuNavigation!! }
    }

    fun addBy(authorityDefinitionId: Long, navigationIdGroup: List<Long>) {
        val authorityDefinition = authorityDefinitionFinder.findByLive(authorityDefinitionId)
        val projectId = authorityDefinition.project!!.id!!
        val menuNavigations = menuNavigationFinder.findByLive(projectId, navigationIdGroup)

        val targetMenuNavigations: MutableList<MenuNavigation> = arrayListOf()

        for (menuNavigation in menuNavigations) {
            putInParentMenuNavigation(targetMenuNavigations, menuNavigation)
        }

        val mappingUnits = targetMenuNavigations.map {
            AuthorityNavigationUnit().apply {
                setBy(it)
                setBy(authorityDefinition)
            }
        }

        authorityNavigationUnitRepository.saveAll(mappingUnits)
    }

    fun createBundle(projectId: Long, name: String, menuNavigationIdGroup: List<Long>): Long {
        val project = projectFinder.findBy(projectId)
        val authorityDefinition = authorityDefinitionInteraction.createBy(project, name)
        this.addBy(authorityDefinition.id!!, menuNavigationIdGroup)

        return authorityDefinitionRepository.save(authorityDefinition).id!!
    }

    fun getBundles(projectId: Long): List<AuthorityDefinition> {
        return authorityDefinitionFinder.findListBy(projectId)
    }

    fun lookInBundle(authorityDefinitionId: Long): List<ReplyOfMenuNavigation> {
        val menuNavigations = this.hasMenuNavigations(authorityDefinitionId)

        return menuNavigations.map {
            val fullUrl = menuNavigationInteraction.getFullUrl(it)
            from(it, fullUrl)
        }
    }

    fun lookInBundleForTreeFormat(authorityDefinitionId: Long): List<MenuTreeResources.Reply.TreeBranch> {
        val menuNavigations = this.hasMenuNavigations(authorityDefinitionId)

        return menuNavigations.map {
            val fullUrl = menuNavigationInteraction.getFullUrl(it)
            val menuNavigationResource = from(it, fullUrl)
            MenuTreeResources.Reply.TreeBranch.of(
                it.treeId,
                it.parentTreeId,
                menuNavigationResource,
            )
        }
    }

    fun mappingNavigationAndDefinition(projectId: Long, authorityDefinitionId: Long, menuNavigationId: Long) {
        val authorityDefinition = authorityDefinitionFinder.findBy(authorityDefinitionId)
        val menuNavigation = menuNavigationFinder.findByMenuNavigationId(projectId, menuNavigationId)

        val mappingUnit = AuthorityNavigationUnit().apply {
            setBy(menuNavigation)
            setBy(authorityDefinition)
        }

        authorityNavigationUnitRepository.save(mappingUnit)
    }

    fun expireBy(projectId: Long, authorityDefinitionId: Long) {
        val authorityDefinition = authorityDefinitionFinder.findByLive(projectId, authorityDefinitionId)

        authorityDefinition.expire()
        authorityDefinitionRepository.save(authorityDefinition)
    }

    fun removeMappingNavigationAndDefinition(projectId: Long, authorityDefinitionId: Long, menuNavigationIdGroup: List<Long>) {
        val mappingUnits = definitionAndNavigationFinder.findMappings(authorityDefinitionId, menuNavigationIdGroup)

        mappingUnits.map { it.expire() }
        authorityNavigationUnitRepository.saveAll(mappingUnits)
    }

    private fun putInParentMenuNavigation(
        menuNavigations: MutableList<MenuNavigation>,
        menuNavigation: MenuNavigation,
    ) {
        menuNavigations.add(menuNavigation)
        val parentMenuNavigation = menuNavigation.parentMenuNavigation
        if (parentMenuNavigation != null && !menuNavigations.contains(parentMenuNavigation)) {
            putInParentMenuNavigation(menuNavigations, parentMenuNavigation)
        }
    }
}

package io.mustelidae.otter.lutrogale.api.domain.migration

import io.mustelidae.otter.lutrogale.api.domain.migration.client.HttpSpecClient
import io.mustelidae.otter.lutrogale.api.domain.migration.graphql.FlatBasePathToMenu
import io.mustelidae.otter.lutrogale.api.domain.migration.graphql.HttpOperation
import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.PolicyException
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectInteraction
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GraphQLMigrationInteraction(
    val httpSpecClient: HttpSpecClient,
    val menuNavigationRepository: MenuNavigationRepository,
    val projectFinder: ProjectFinder,
    val projectInteraction: ProjectInteraction,
) {

    @Transactional(readOnly = true)
    fun preview(
        url: String,
        httpOperation: HttpOperation,
        headers: List<Pair<String, Any>>?,
    ): String {
        val projectId = projectInteraction.register("migration", "preview")
        val project = projectFinder.findBy(projectId)

        val scheme = httpSpecClient.getGraphQLSpec(url, headers)
        val pathToMenu = FlatBasePathToMenu(project, scheme, httpOperation)

        pathToMenu.makeTree(menuNavigationRepository)

        return pathToMenu.printMenuTree()
    }

    @Transactional
    fun generate(
        projectId: Long,
        url: String,
        httpOperation: HttpOperation,
        headers: List<Pair<String, Any>>?,
    ): Long {
        val project = projectFinder.findBy(projectId)
        if (project.menuNavigations.size > 1) {
            throw PolicyException(
                DefaultError(
                    ErrorCode.PL03,
                    "Project has more than one menuNavigations",
                ),
            )
        }

        val scheme = httpSpecClient.getGraphQLSpec(url, headers)
        val pathToMenu = FlatBasePathToMenu(project, scheme, httpOperation)

        pathToMenu.makeTree(menuNavigationRepository)

        return menuNavigationRepository.save(pathToMenu.rootMenuNavigation).id!!
    }
}

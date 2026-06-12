package io.mustelidae.otter.lutrogale.api.domain.migration

import io.mustelidae.otter.lutrogale.api.domain.migration.client.HttpSpecClient
import io.mustelidae.otter.lutrogale.api.domain.migration.graphql.FlatBaseGraphQLSyncToMenu
import io.mustelidae.otter.lutrogale.api.domain.migration.graphql.FlatBasePathToMenu
import io.mustelidae.otter.lutrogale.api.domain.migration.graphql.HttpOperation
import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.PolicyException
import io.mustelidae.otter.lutrogale.config.PreconditionFailException
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation.ListStructure
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectInteraction
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GraphQLMigrationInteraction(
    val httpSpecClient: HttpSpecClient,
    val menuNavigationRepository: MenuNavigationRepository,
    val projectFinder: ProjectFinder,
    val projectInteraction: ProjectInteraction,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun preview(
        url: String,
        httpOperation: HttpOperation,
        headers: List<Pair<String, Any>>?,
    ): String {
        val project =
            Project.of("migration", "preview", ListStructure.FLAT).apply {
                addBy(
                    io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
                        .root(),
                )
            }

        val scheme = httpSpecClient.fetchGraphQLSpec(url, headers)
        val pathToMenu = FlatBasePathToMenu(project, scheme, httpOperation)

        pathToMenu.makeTree(DummyMenuNavigationRepository())

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

        val scheme = httpSpecClient.fetchGraphQLSpec(url, headers)
        val pathToMenu = FlatBasePathToMenu(project, scheme, httpOperation)

        pathToMenu.makeTree(menuNavigationRepository)

        return menuNavigationRepository.save(pathToMenu.rootMenuNavigation).id!!
    }

    @Transactional
    fun sync(projectId: Long) {
        val project = projectFinder.findBy(projectId)
        val migrationUrl =
            project.migrationUrl
                ?: throw PreconditionFailException("Sync requires a migration spec URL.")

        log.info("Starting GraphQL sync for project: ${project.name}")

        val scheme = httpSpecClient.fetchGraphQLSpec(migrationUrl, null)
        val syncToMenu = FlatBaseGraphQLSyncToMenu(project, scheme)
        syncToMenu.makeTree(menuNavigationRepository)

        log.info("Completed GraphQL sync for project: ${project.name}")
    }
}

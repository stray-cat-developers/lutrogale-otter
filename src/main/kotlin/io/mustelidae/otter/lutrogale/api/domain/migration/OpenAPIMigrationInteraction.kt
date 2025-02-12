package io.mustelidae.otter.lutrogale.api.domain.migration

import io.mustelidae.otter.lutrogale.api.domain.migration.api.MigrationResources
import io.mustelidae.otter.lutrogale.api.domain.migration.api.MigrationResources.Request.OpenAPI.MigrationType.FLAT
import io.mustelidae.otter.lutrogale.api.domain.migration.api.MigrationResources.Request.OpenAPI.MigrationType.TREE
import io.mustelidae.otter.lutrogale.api.domain.migration.client.HttpSpecClient
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.FlatBasePathToMenu
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.TreeBasePathToMenu
import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.PolicyException
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectInteraction
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OpenAPIMigrationInteraction(
    val httpSpecClient: HttpSpecClient,
    val menuNavigationRepository: MenuNavigationRepository,
    val projectFinder: ProjectFinder,
    private val projectInteraction: ProjectInteraction,
) {

    @Transactional(readOnly = true)
    fun preview(
        url: String,
        swaggerSpecType: SwaggerSpec.Type,
        migrationType: MigrationResources.Request.OpenAPI.MigrationType,
        headers: List<Pair<String, Any>>?,
    ): String {
        val projectId = projectInteraction.register("migration", "preview")
        val project = projectFinder.findBy(projectId)
        val rootMenuNavigation = project.menuNavigations.first()

        val pathToMenu: PathToMenu = pathToMenuUsingOpenAPI(url, swaggerSpecType, headers, migrationType, rootMenuNavigation)
        pathToMenu.makeTree(menuNavigationRepository)

        return pathToMenu.printMenuTree()
    }

    @Transactional
    fun generate(
        projectId: Long,
        url: String,
        swaggerSpecType: SwaggerSpec.Type,
        migrationType: MigrationResources.Request.OpenAPI.MigrationType,
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
        val rootMenuNavigation = project.menuNavigations.first()

        val pathToMenu: PathToMenu =
            pathToMenuUsingOpenAPI(url, swaggerSpecType, headers, migrationType, rootMenuNavigation)
        pathToMenu.makeTree(menuNavigationRepository)

        return menuNavigationRepository.save(pathToMenu.rootMenuNavigation).id!!
    }

    private fun pathToMenuUsingOpenAPI(
        url: String,
        swaggerSpecType: SwaggerSpec.Type,
        headers: List<Pair<String, Any>>?,
        migrationType: MigrationResources.Request.OpenAPI.MigrationType,
        rootMenuNavigation: MenuNavigation,
    ): PathToMenu {
        val openAPIJson = httpSpecClient.getOpenAPISpec(url, swaggerSpecType, headers)
        val swaggerSpec = SwaggerSpec(openAPIJson, swaggerSpecType)

        val pathToMenu: PathToMenu = when (migrationType) {
            TREE -> TreeBasePathToMenu(swaggerSpec.openAPI, rootMenuNavigation)
            FLAT -> FlatBasePathToMenu(swaggerSpec.openAPI, rootMenuNavigation)
        }
        return pathToMenu
    }
}

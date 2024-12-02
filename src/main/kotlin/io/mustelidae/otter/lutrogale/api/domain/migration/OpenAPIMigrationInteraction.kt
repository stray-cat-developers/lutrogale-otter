package io.mustelidae.otter.lutrogale.api.domain.migration

import io.mustelidae.otter.lutrogale.api.domain.migration.api.MigrationResources
import io.mustelidae.otter.lutrogale.api.domain.migration.api.MigrationResources.Request.OpenAPI.MigrationType.FLAT
import io.mustelidae.otter.lutrogale.api.domain.migration.api.MigrationResources.Request.OpenAPI.MigrationType.TREE
import io.mustelidae.otter.lutrogale.api.domain.migration.client.RestStyleMigrationClient
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.FlatBasePathToMenu
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.PathToMenu
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.TreeBasePathToMenu
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OpenAPIMigrationInteraction(
    val restStyleMigrationClient: RestStyleMigrationClient,
    val menuNavigationRepository: MenuNavigationRepository,
    val projectFinder: ProjectFinder,
) {

    fun preview(
        url: String,
        swaggerSpecType: SwaggerSpec.Type,
        migrationType: MigrationResources.Request.OpenAPI.MigrationType,
        headers: List<Pair<String, Any>>?,
    ): String {
        val openAPIJson = restStyleMigrationClient.getOpenAPISpec(url, swaggerSpecType, headers)
        val swaggerSpec = SwaggerSpec(openAPIJson, swaggerSpecType)
        val rootMenuNavigation = MenuNavigation.root()

        val pathToMenu: PathToMenu = when (migrationType) {
            TREE -> TreeBasePathToMenu(swaggerSpec.openAPI, rootMenuNavigation)
            FLAT -> FlatBasePathToMenu(swaggerSpec.openAPI, rootMenuNavigation)
        }
        pathToMenu.makeTree()

        return pathToMenu.printMenuTree()
    }

    fun generate(
        projectId: Long,
        url: String,
        swaggerSpecType: SwaggerSpec.Type,
        migrationType: MigrationResources.Request.OpenAPI.MigrationType,
        headers: List<Pair<String, Any>>?,
    ): Long {
        val project = projectFinder.findBy(projectId)
        if (project.menuNavigations.size > 1) {
            throw IllegalArgumentException("Project has more than one menuNavigations")
        }
        val rootMenuNavigation = project.menuNavigations.first()

        val openAPIJson = restStyleMigrationClient.getOpenAPISpec(url, swaggerSpecType, headers)
        val swaggerSpec = SwaggerSpec(openAPIJson, swaggerSpecType)

        val pathToMenu: PathToMenu = when (migrationType) {
            TREE -> TreeBasePathToMenu(swaggerSpec.openAPI, rootMenuNavigation)
            FLAT -> FlatBasePathToMenu(swaggerSpec.openAPI, rootMenuNavigation)
        }
        pathToMenu.makeTree()

        return menuNavigationRepository.save(pathToMenu.rootMenuNavigation).id!!
    }
}

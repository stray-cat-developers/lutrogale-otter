package io.mustelidae.otter.lutrogale.api.domain.migration

import io.mustelidae.otter.lutrogale.api.domain.migration.client.RestStyleMigrationClient
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.PathToMenu
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.TreeBasePathToMenu
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import org.springframework.stereotype.Service

@Service
class MigrationInteraction(
    val restStyleMigrationClient: RestStyleMigrationClient,
    val menuNavigationRepository: MenuNavigationRepository,
) {

    fun prepareGenerateTreeFromOpenAPI(url: String, swaggerSpecType: SwaggerSpec.Type, headers: List<Pair<String, Any>>?): String {
        val openAPIJson = restStyleMigrationClient.getOpenAPISpec(url, swaggerSpecType, headers)
        val swaggerSpec = SwaggerSpec(openAPIJson, swaggerSpecType)

        val pathToMenu: PathToMenu = TreeBasePathToMenu(swaggerSpec.openAPI)

        pathToMenu.makeTree()
        return pathToMenu.printMenuTree()
    }

    fun generateTreeFromOpenAPI(url: String, swaggerSpecType: SwaggerSpec.Type, headers: List<Pair<String, Any>>?): Long {
        val openAPIJson = restStyleMigrationClient.getOpenAPISpec(url, swaggerSpecType, headers)
        val swaggerSpec = SwaggerSpec(openAPIJson, swaggerSpecType)

        val pathToMenu: PathToMenu = TreeBasePathToMenu(swaggerSpec.openAPI)

        pathToMenu.makeTree()

        return menuNavigationRepository.save(pathToMenu.rootMenuNavigation).id!!
    }
}

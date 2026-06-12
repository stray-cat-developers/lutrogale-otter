package io.mustelidae.otter.lutrogale.api.domain.migration

import io.mustelidae.otter.lutrogale.api.domain.migration.api.MigrationResources
import io.mustelidae.otter.lutrogale.api.domain.migration.api.MigrationResources.Request.OpenAPI.MigrationType.FLAT
import io.mustelidae.otter.lutrogale.api.domain.migration.api.MigrationResources.Request.OpenAPI.MigrationType.TREE
import io.mustelidae.otter.lutrogale.api.domain.migration.client.HttpSpecClient
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.FlatBasePathToMenu
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.FlatBaseSyncToMenu
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.TreeBasePathToMenu
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.TreeBaseSyncToMenu
import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.DevelopMistakeException
import io.mustelidae.otter.lutrogale.config.PolicyException
import io.mustelidae.otter.lutrogale.config.PreconditionFailException
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation.ListStructure
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectInteraction
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OpenAPIMigrationInteraction(
    val httpSpecClient: HttpSpecClient,
    val menuNavigationRepository: MenuNavigationRepository,
    val projectFinder: ProjectFinder,
    private val projectInteraction: ProjectInteraction,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun preview(
        url: String,
        swaggerSpecType: SwaggerSpec.Type,
        migrationType: MigrationResources.Request.OpenAPI.MigrationType,
        headers: List<Pair<String, Any>>?,
    ): String {
        val listStructure =
            when (migrationType) {
                FLAT -> ListStructure.FLAT
                TREE -> ListStructure.TREE
            }
        val project =
            Project.of("migration", "preview", listStructure).apply {
                addBy(MenuNavigation.root())
            }
        val rootMenuNavigation = project.menuNavigations.first()

        val pathToMenu: PathToMenu = pathToMenuUsingOpenAPI(url, swaggerSpecType, headers, migrationType, rootMenuNavigation)
        pathToMenu.makeTree(DummyMenuNavigationRepository())

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
        val openAPIJson = httpSpecClient.fetchOpenAPISpec(url, swaggerSpecType, headers)
        val swaggerSpec = SwaggerSpec(openAPIJson, swaggerSpecType)

        return when (migrationType) {
            TREE -> TreeBasePathToMenu(swaggerSpec.openAPI, rootMenuNavigation)
            FLAT -> FlatBasePathToMenu(swaggerSpec.openAPI, rootMenuNavigation)
        }
    }

    @Transactional
    fun sync(projectId: Long) {
        val project = projectFinder.findBy(projectId)
        val migrationUrl =
            project.migrationUrl
                ?: throw PreconditionFailException("Sync requires a migration spec URL.")

        log.info("Starting OpenAPI sync for project: ${project.name}")

        val swaggerSpecType =
            when (project.specType) {
                Project.SpecType.OPENAPI_JSON -> SwaggerSpec.Type.JSON
                Project.SpecType.OPENAPI_YAML -> SwaggerSpec.Type.YAML
                else -> throw DevelopMistakeException("Unexpected specType for OpenAPI sync: ${project.specType}")
            }

        val spec = httpSpecClient.fetchOpenAPISpec(migrationUrl, swaggerSpecType, null)
        val swaggerSpec = SwaggerSpec(spec, swaggerSpecType)
        val rootMenuNavigation = project.menuNavigations.first { it.isRoot() }

        val syncToMenu =
            when (project.listStructure) {
                ListStructure.FLAT -> FlatBaseSyncToMenu(swaggerSpec.openAPI, rootMenuNavigation)
                ListStructure.TREE -> TreeBaseSyncToMenu(swaggerSpec.openAPI, rootMenuNavigation)
            }
        syncToMenu.makeTree(menuNavigationRepository)

        log.info("Completed OpenAPI sync for project: ${project.name}")
    }
}

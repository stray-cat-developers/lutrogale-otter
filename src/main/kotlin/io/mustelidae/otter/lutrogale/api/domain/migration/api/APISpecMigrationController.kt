package io.mustelidae.otter.lutrogale.api.domain.migration.api

import io.mustelidae.otter.lutrogale.api.domain.migration.GraphQLMigrationInteraction
import io.mustelidae.otter.lutrogale.api.domain.migration.OpenAPIMigrationInteraction
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec
import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectInteraction
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "마이그레이션", description = "OpenAPI Spec 마이그레이션을 수행합니다.")
@LoginCheck(false)
@RestController
@RequestMapping("/v1/migration/")
class APISpecMigrationController(
    private val openAPIMigrationInteraction: OpenAPIMigrationInteraction,
    private val graphQLMigrationInteraction: GraphQLMigrationInteraction,
    private val projectInteraction: ProjectInteraction,
) {
    @PostMapping("/openapi/preview")
    @ResponseStatus(HttpStatus.OK)
    fun previewOpenAPI(
        @RequestBody request: MigrationResources.Request.OpenAPI,
    ): Reply<String> {
        val swaggerSpecType = SwaggerSpec.Type.valueOf(request.format.name)
        val header = request.header?.map { it.key to it.value }?.toList()
        return openAPIMigrationInteraction.preview(request.url, swaggerSpecType, request.migrationType, header).toReply()
    }

    @PostMapping("/openapi/generate/project/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun generateOpenAPI(
        @PathVariable projectId: Long,
        @RequestBody request: MigrationResources.Request.OpenAPI,
    ): Reply<Long> {
        val swaggerSpecType = SwaggerSpec.Type.valueOf(request.format.name)
        val header = request.header?.map { it.key to it.value }?.toList()
        return openAPIMigrationInteraction.generate(projectId, request.url, swaggerSpecType, request.migrationType, header).toReply()
    }

    @PostMapping("/openapi/sync/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    fun syncOpenAPI(
        @PathVariable projectId: Long,
        @RequestBody request: MigrationResources.Request.OpenAPISync,
    ): Reply<String> {
        val specType =
            when (request.format) {
                MigrationResources.Request.OpenAPI.OpenAPIFormat.JSON -> Project.SpecType.OPENAPI_JSON
                MigrationResources.Request.OpenAPI.OpenAPIFormat.YAML -> Project.SpecType.OPENAPI_YAML
            }
        projectInteraction.updateSync(projectId, specType, request.url)
        return "Sync configured successfully".toReply()
    }

    @PostMapping("/graphql/preview")
    @ResponseStatus(HttpStatus.OK)
    fun previewGraphQL(
        @RequestBody request: MigrationResources.Request.GraphQL,
    ): Reply<String> {
        val header = request.header?.map { it.key to it.value }?.toList()
        return graphQLMigrationInteraction.preview(request.url, request.httpOperation, header).toReply()
    }

    @PostMapping("/graphql/generate/project/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun generateGraphQL(
        @PathVariable projectId: Long,
        @RequestBody request: MigrationResources.Request.GraphQL,
    ): Reply<Long> {
        val header = request.header?.map { it.key to it.value }?.toList()
        return graphQLMigrationInteraction.generate(projectId, request.url, request.httpOperation, header).toReply()
    }

    @PostMapping("/graphql/sync/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    fun syncGraphQL(
        @PathVariable projectId: Long,
        @RequestBody request: MigrationResources.Request.GraphQLSync,
    ): Reply<String> {
        projectInteraction.updateSync(projectId, Project.SpecType.GRAPHQL, request.url)
        return "Sync configured successfully".toReply()
    }
}

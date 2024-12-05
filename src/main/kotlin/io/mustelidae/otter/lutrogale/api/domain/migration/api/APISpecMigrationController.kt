package io.mustelidae.otter.lutrogale.api.domain.migration.api

import io.mustelidae.otter.lutrogale.api.domain.migration.GraphQLMigrationInteraction
import io.mustelidae.otter.lutrogale.api.domain.migration.OpenAPIMigrationInteraction
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec
import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
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
) {

    @PostMapping("/openapi/preview")
    @ResponseStatus(HttpStatus.OK)
    fun previewOpenAPI(@RequestBody request: MigrationResources.Request.OpenAPI): Reply<String> {
        val swaggerSpecType = SwaggerSpec.Type.valueOf(request.format.name)
        val header = request.header?.map { it.key to it.value }?.toList()

        val preview = openAPIMigrationInteraction.preview(request.url, swaggerSpecType, request.migrationType, header)

        return preview.toReply()
    }

    @PostMapping("/openapi/generate/project/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun generateOpenAPI(@PathVariable projectId: Long, @RequestBody request: MigrationResources.Request.OpenAPI): Reply<Long> {
        val swaggerSpecType = SwaggerSpec.Type.valueOf(request.format.name)
        val header = request.header?.map { it.key to it.value }?.toList()

        val rootMenuId = openAPIMigrationInteraction.generate(projectId, request.url, swaggerSpecType, request.migrationType, header)
        return rootMenuId.toReply()
    }

    @PostMapping("/graphql/preview")
    @ResponseStatus(HttpStatus.OK)
    fun previewGraphQL(@RequestBody request: MigrationResources.Request.GraphQL): Reply<String> {
        val header = request.header?.map { it.key to it.value }?.toList()

        val preview = graphQLMigrationInteraction.preview(request.url, request.httpOperation, header)

        return preview.toReply()
    }

    @PostMapping("/graphql/generate/project/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun generateGraphQL(@PathVariable projectId: Long, @RequestBody request: MigrationResources.Request.GraphQL): Reply<Long> {
        val header = request.header?.map { it.key to it.value }?.toList()

        val rootMenuId = graphQLMigrationInteraction.generate(projectId, request.url, request.httpOperation, header)
        return rootMenuId.toReply()
    }
}

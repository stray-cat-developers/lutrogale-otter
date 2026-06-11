package io.mustelidae.otter.lutrogale.api.domain.migration.api

import io.mustelidae.otter.lutrogale.api.domain.migration.graphql.HttpOperation
import io.swagger.v3.oas.annotations.media.Schema

class MigrationResources {
    class Request {
        @Schema(name = "Lutrogale.Migration.Request.OpenAPI")
        data class OpenAPI(
            val url: String,
            val version: String,
            val format: OpenAPIFormat,
            val migrationType: MigrationType,
            val header: List<HttpHeader>? = null,
        ) {
            enum class MigrationType {
                TREE,
                FLAT,
            }

            enum class OpenAPIFormat {
                JSON,
                YAML,
            }
        }

        @Schema(name = "Lutrogale.Migration.Request.GraphQL")
        data class GraphQL(
            val url: String,
            val httpOperation: HttpOperation,
            val header: List<HttpHeader>? = null,
        )

        @Schema(name = "Lutrogale.Migration.Request.HttpHeader")
        data class HttpHeader(
            val key: String,
            val value: String,
        )
    }
}

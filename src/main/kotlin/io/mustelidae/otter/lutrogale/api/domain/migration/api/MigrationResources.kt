package io.mustelidae.otter.lutrogale.api.domain.migration.api

import io.mustelidae.otter.lutrogale.api.domain.migration.graphql.HttpOperation

class MigrationResources {
    data class HttpHeader(
        val key: String,
        val value: String,
    )

    class Request {
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

        data class GraphQL(
            val url: String,
            val httpOperation: HttpOperation,
            val header: List<HttpHeader>? = null,
        )
    }
}

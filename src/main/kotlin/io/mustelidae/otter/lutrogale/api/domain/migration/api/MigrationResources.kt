package io.mustelidae.otter.lutrogale.api.domain.migration.api

class MigrationResources {

    class Request {
        data class OpenAPI(
            val uri: String,
            val version: String,
            val format: OpenAPIFormat,
            val migrationType: MigrationType,
            val header: List<Header>? = null,
        ) {
            enum class MigrationType {
                TREE, FLAT
            }

            enum class OpenAPIFormat {
                JSON, YAML
            }

            data class Header(
                val key: String,
                val value: String,
            )
        }
    }
}

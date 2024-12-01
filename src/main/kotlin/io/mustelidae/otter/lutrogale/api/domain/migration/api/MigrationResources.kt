package io.mustelidae.otter.lutrogale.api.domain.migration.api

class MigrationResources {

    class Request {
        data class OpenAPI(
            val uri: String,
            val version: String,
            val format: OpenAPIFormat,
            val header: List<Header>,
        ) {
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

package io.mustelidae.otter.lutrogale.api.domain.migration.api

import org.hibernate.validator.constraints.URL
import java.net.URI

class MigrationResources {

    class Request {
        data class OpenAPI(
            val uri: String,
            val version: String,
            val format: OpenAPIFormat
        ) {
            enum class OpenAPIFormat {
                JSON, YAML
            }
        }
    }
}
package io.mustelidae.otter.lutrogale.api.domain.migration.client

import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec

interface RestStyleMigrationClient {

    fun getOpenAPISpec(url: String, type: SwaggerSpec.Type): String
}
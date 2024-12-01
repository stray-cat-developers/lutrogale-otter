package io.mustelidae.otter.lutrogale.api.domain.migration.client

import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec

class DummyRestStyleMigrationClient : RestStyleMigrationClient {
    override fun getOpenAPISpec(url: String, type: SwaggerSpec.Type): String {
        TODO("Not yet implemented")
    }
}

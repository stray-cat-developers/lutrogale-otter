package io.mustelidae.otter.lutrogale.api.domain.migration.client

import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.springframework.web.client.RestClient

class StableRestStyleMigrationClient(
    private val restClient: CloseableHttpClient
): RestStyleMigrationClient {
    override fun getOpenAPISpec(url: String, type: SwaggerSpec.Type): String {
        TODO("Not yet implemented")
    }
}
package io.mustelidae.otter.lutrogale.api.domain.migration.client

import io.mustelidae.grantotter.utils.ConnectionConfig
import io.mustelidae.grantotter.utils.RestClient
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class StableRestStyleMigrationClientTest {

    @Test
    @Disabled
    fun getOpenAPISpecJson() {
        val env = ConnectionConfig(
            1000,
            1000,
            1,
            1,
            1000,
        )
        val stableRestStyleMigrationClient = StableRestStyleMigrationClient(RestClient.new(env), true)
        val openAPISpec = stableRestStyleMigrationClient.getOpenAPISpec("https://petstore.swagger.io/v2/swagger.json", SwaggerSpec.Type.JSON, null)

        assertNotNull(openAPISpec)
    }

    @Test
    @Disabled
    fun getOpenAPISpecYaml() {
        val env = ConnectionConfig(
            1000,
            1000,
            1,
            1,
            1000,
        )
        val stableRestStyleMigrationClient = StableRestStyleMigrationClient(RestClient.new(env), true)
        val openAPISpec = stableRestStyleMigrationClient.getOpenAPISpec("https://petstore.swagger.io/v2/swagger.yaml", SwaggerSpec.Type.YAML, null)

        assertNotNull(openAPISpec)
    }
}

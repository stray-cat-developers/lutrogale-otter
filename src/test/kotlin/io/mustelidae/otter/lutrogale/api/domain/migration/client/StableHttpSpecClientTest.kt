package io.mustelidae.otter.lutrogale.api.domain.migration.client

import io.mustelidae.grantotter.utils.ConnectionConfig
import io.mustelidae.grantotter.utils.RestClient
import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class StableHttpSpecClientTest {

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
        val stableRestStyleMigrationClient = StableHttpSpecClient(RestClient.new(env), true)
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
        val stableHttpSpecClient = StableHttpSpecClient(RestClient.new(env), true)
        val openAPISpec = stableHttpSpecClient.getOpenAPISpec("https://petstore.swagger.io/v2/swagger.yaml", SwaggerSpec.Type.YAML, null)

        assertNotNull(openAPISpec)
    }

    @Test
    fun graphQLSpec() {
        val env = ConnectionConfig(
            1000,
            1000,
            1,
            1,
            1000,
        )
        val stableHttpSpecClient = StableHttpSpecClient(RestClient.new(env), true)
        val graphQLSpec = stableHttpSpecClient.getGraphQLSpec("https://raw.githubusercontent.com/marmelab/GraphQL-example/refs/heads/master/schema.graphql", null)
        assertNotNull(graphQLSpec)
    }
}

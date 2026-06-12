package io.mustelidae.otter.lutrogale.api.domain.migration.client

import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec

interface HttpSpecClient {
    fun fetchOpenAPISpec(
        url: String,
        type: SwaggerSpec.Type,
        headers: List<Pair<String, Any>>?,
    ): String

    fun fetchGraphQLSpec(
        url: String,
        headers: List<Pair<String, Any>>?,
    ): String
}

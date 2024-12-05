package io.mustelidae.otter.lutrogale.api.domain.migration.client

import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec
import io.mustelidae.otter.lutrogale.utils.Jackson
import io.mustelidae.otter.lutrogale.utils.RestClientSupport
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.core5.http.ContentType
import org.slf4j.LoggerFactory

class StableHttpSpecClient(
    private val restClient: CloseableHttpClient,
    writeLog: Boolean,
) : HttpSpecClient, RestClientSupport(
    Jackson.getMapper(),
    writeLog,
    LoggerFactory.getLogger(StableHttpSpecClient::class.java),
) {
    override fun getOpenAPISpec(url: String, type: SwaggerSpec.Type, headers: List<Pair<String, Any>>?): String {
        val requestHeader = mutableListOf<Pair<String, Any>>()

        headers?.let {
            requestHeader.addAll(it)
        }

        val contentType = when (type) {
            SwaggerSpec.Type.JSON -> Pair("Content-Type", ContentType.APPLICATION_JSON)
            SwaggerSpec.Type.YAML -> Pair("Content-Type", "application/yaml")
        }

        requestHeader.add(contentType)
        return restClient.get(url, requestHeader)
            .orElseThrow()
    }

    override fun getGraphQLSpec(url: String, headers: List<Pair<String, Any>>?): String {
        val requestHeader = mutableListOf<Pair<String, Any>>()

        headers?.let {
            requestHeader.addAll(it)
        }

        requestHeader.add(Pair("Content-Type", ContentType.TEXT_PLAIN))
        return restClient.get(url, requestHeader)
            .orElseThrow()
    }
}

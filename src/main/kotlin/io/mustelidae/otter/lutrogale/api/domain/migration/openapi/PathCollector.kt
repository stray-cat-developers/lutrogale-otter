package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.swagger.v3.oas.models.OpenAPI
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod

class PathCollector(private val openAPI: OpenAPI) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun collectPathAndMethods(): List<Pair<String, List<HttpMethod>>> {
        val pathAndMethods = openAPI.paths.map { (path, pathItem) ->
            val methods = pathItem.readOperationsMap().keys.map { HttpMethod.valueOf(it.name.uppercase())  }
            Pair(path, methods)
        }

        log.debug("Collected path and methods: {}", pathAndMethods)
        return pathAndMethods
    }
}
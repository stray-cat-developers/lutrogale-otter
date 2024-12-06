package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.swagger.v3.oas.models.OpenAPI
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMethod

class PathCollector(private val openAPI: OpenAPI) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun collectPathAndMethods(): List<HttpAPISpec> {
        val pathAndMethods = openAPI.paths.map { (path, pathItem) ->
            val methods = pathItem.readOperationsMap().keys.map { RequestMethod.valueOf(it.name.uppercase()) }
            val summary = pathItem.summary
            HttpAPISpec(path, methods, summary)
        }

        log.debug("Collected path and methods: {}", pathAndMethods)
        return pathAndMethods
    }
}

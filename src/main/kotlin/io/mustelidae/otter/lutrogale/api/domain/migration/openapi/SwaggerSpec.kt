package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import com.fasterxml.jackson.databind.JsonNode
import io.mustelidae.otter.lutrogale.utils.Jackson
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.converter.SwaggerConverter
import io.swagger.v3.parser.core.models.ParseOptions
import org.springframework.data.util.Version

class SwaggerSpec(
    private val originalJson: String,
    val type: Type
) {
    val openAPI: OpenAPI

    init {
        val specVersion = getVersion() ?: throw IllegalArgumentException("Version information does not exist.")
        val version = Version.parse(specVersion)
        val parseOptions = ParseOptions()

        // open api 2.X
        openAPI = if (version.isLessThan(Version(3, 0, 0))) {
            SwaggerConverter().readContents(originalJson, null, parseOptions).openAPI
        } else {
            OpenAPIV3Parser().readContents(originalJson, null, parseOptions).openAPI
        }
    }


    private fun getVersion(): String? {
        val versionNode: JsonNode? = when (type) {
            Type.JSON -> {
                try {
                    val treeNode = Jackson.getMapper().readTree(originalJson)
                    treeNode.get("swagger") ?: treeNode.get("openapi")
                } catch (e: Exception) {
                    throw IllegalStateException("The format of the Open API Spec is not Json Type.", e)
                }
            }
            Type.YAML -> {
                try {
                    val treeNode = Jackson.getYmlMapper().readTree(originalJson)
                    treeNode.get("swagger") ?: treeNode.get("openapi")
                } catch (e: Exception) {
                    throw IllegalStateException("The format of the Open API Spec is not YAML Type.")
                }
            }
        }

        return versionNode?.textValue()
    }

    enum class Type {
        JSON,
        YAML,
    }
}
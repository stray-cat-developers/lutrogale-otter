package io.mustelidae.otter.lutrogale.config

import io.swagger.v3.core.converter.ModelConverters
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfiguration {

    @Bean
    fun default(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("API")
        .addOpenApiCustomizer {
            it.info.version("v1")
            it.components.schemas.putAll(ModelConverters.getInstance().read(GlobalErrorFormat::class.java))
        }
        .packagesToScan("io.mustelidae.otter.lutrogale")
        .pathsToMatch("/v1/**")
        .pathsToExclude("/v1/maintenance/**", "/v1/migration/**", "/v1/bridge/**")
        .build()

    @Bean
    fun maintenance(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("Maintenance")
        .addOpenApiCustomizer {
            it.info.version("v1")
            it.components.schemas.putAll(ModelConverters.getInstance().read(GlobalErrorFormat::class.java))
        }
        .packagesToScan("io.mustelidae.otter.lutrogale")
        .pathsToMatch("/v1/maintenance/**")
        .build()
}

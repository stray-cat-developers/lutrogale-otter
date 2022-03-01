package io.mustelidae.otter.lutrogale.api.config

import io.swagger.v3.core.converter.ModelConverters
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfiguration {

    @Bean
    fun default(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("API")
        .addOpenApiCustomiser {
            it.info.version("v1")
            it.components.schemas.putAll(ModelConverters.getInstance().read(GlobalErrorFormat::class.java))
        }
        .packagesToScan("io.mustelidae.otter.neotropical.api.domain")
        .pathsToMatch("/v1/**")
        .pathsToExclude("/v1/maintenance/**", "/v1/migration/**", "/v1/bridge/**")
        .build()

    @Bean
    fun maintenance(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("Maintenance")
        .addOpenApiCustomiser {
            it.info.version("v1")
            it.components.schemas.putAll(ModelConverters.getInstance().read(GlobalErrorFormat::class.java))
        }
        .packagesToScan("io.mustelidae.otter.neotropical.api.domain")
        .pathsToMatch("/v1/maintenance/**")
        .build()

    @Bean
    fun migration(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("Migration")
        .addOpenApiCustomiser {
            it.info.version("v1")
            it.components.schemas.putAll(ModelConverters.getInstance().read(GlobalErrorFormat::class.java))
        }
        .packagesToScan("io.mustelidae.otter.neotropical.api.domain")
        .pathsToMatch("/v1/migration/**")
        .build()

    @Bean
    fun bridge(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("Bridge")
        .addOpenApiCustomiser {
            it.info.version("v1")
            it.components.schemas.putAll(ModelConverters.getInstance().read(GlobalErrorFormat::class.java))
        }
        .packagesToScan("io.mustelidae.otter.neotropical.api.domain")
        .pathsToMatch("/v1/bridge/**")
        .build()
}

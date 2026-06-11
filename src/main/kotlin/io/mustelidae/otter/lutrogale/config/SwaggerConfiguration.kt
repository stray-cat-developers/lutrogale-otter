package io.mustelidae.otter.lutrogale.config

import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources
import io.mustelidae.otter.lutrogale.api.domain.migration.api.MigrationResources
import io.mustelidae.otter.lutrogale.web.domain.admin.api.AdminResources
import io.mustelidae.otter.lutrogale.web.domain.authority.api.AuthorityBundleResources
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.MenuTreeResources
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.NavigationResources
import io.mustelidae.otter.lutrogale.web.domain.project.api.ProjectResources
import io.mustelidae.otter.lutrogale.web.domain.user.api.UserResources
import io.swagger.v3.core.converter.ModelConverters
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfiguration {
    // SpringDoc이 Replies<T>의 타입 파라미터 T를 components/schemas에 자동 등록하지 않으므로
    // Replies<T>와 Reply<T>의 반환 타입으로만 사용되는 DTO를 수동으로 등록한다.
    private val sharedSchemas =
        listOf(
            AccessResources.AccessUri::class.java,
            AccessResources.AccessGraphQL::class.java,
            AccessResources.Reply.AccessState::class.java,
            MigrationResources.HttpHeader::class.java,
            AdminResources.AdminRow::class.java,
            AuthorityBundleResources.Reply.AuthorityBundle::class.java,
            MenuTreeResources.Reply.TreeBranch::class.java,
            NavigationResources.Reply.ReplyOfMenuNavigation::class.java,
            ProjectResources.Reply::class.java,
            UserResources.Reply.BatchRegister::class.java,
            UserResources.Reply.Detail::class.java,
            UserResources.Reply.Simple::class.java,
        )

    private fun registerSchemas(openApi: io.swagger.v3.oas.models.OpenAPI) {
        val components =
            openApi.components ?: io.swagger.v3.oas.models
                .Components()
                .also { openApi.components = it }
        if (components.schemas == null) components.schemas = mutableMapOf()

        fun register(cls: Class<*>) {
            val resolved = ModelConverters.getInstance().readAllAsResolvedSchema(cls) ?: return
            components.schemas.putIfAbsent(resolved.schema.name ?: cls.simpleName, resolved.schema)
            resolved.referencedSchemas.forEach { (name, schema) ->
                components.schemas.putIfAbsent(name, schema)
            }
        }
        register(GlobalErrorFormat::class.java)
        sharedSchemas.forEach { register(it) }
    }

    @Bean
    fun default(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("API")
            .addOpenApiCustomizer {
                it.info.version("v1")
                registerSchemas(it)
            }.packagesToScan("io.mustelidae.otter.lutrogale.api")
            .build()

    @Bean
    fun maintenance(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("Maintenance")
            .addOpenApiCustomizer {
                it.info.version("v1")
                registerSchemas(it)
            }.packagesToScan("io.mustelidae.otter.lutrogale.web")
            .build()
}

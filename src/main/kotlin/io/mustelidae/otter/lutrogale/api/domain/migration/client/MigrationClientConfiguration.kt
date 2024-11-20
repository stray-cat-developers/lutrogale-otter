package io.mustelidae.otter.lutrogale.api.domain.migration.client

import io.mustelidae.otter.lutrogale.config.AppEnvironment
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class MigrationClientConfiguration(
    private val appEnvironment: AppEnvironment
) {

    @Bean
    fun restStyleMigrationClient(): RestClient {
        val env = appEnvironment.default

        return if(env.useDummy) {

        } else {

        }
    }
}
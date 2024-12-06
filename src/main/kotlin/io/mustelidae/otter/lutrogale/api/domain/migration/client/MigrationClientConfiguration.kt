package io.mustelidae.otter.lutrogale.api.domain.migration.client

import io.mustelidae.grantotter.utils.ConnectionConfig
import io.mustelidae.grantotter.utils.RestClient
import io.mustelidae.otter.lutrogale.config.AppEnvironment
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MigrationClientConfiguration(
    private val appEnvironment: AppEnvironment,
) {

    @Bean
    fun httpSpecClient(): HttpSpecClient {
        val env = appEnvironment.default

        return if (env.useDummy) {
            DummyHttpSpecClient()
        } else {
            StableHttpSpecClient(RestClient.new(ConnectionConfig.from(appEnvironment.default)), env.logging)
        }
    }
}

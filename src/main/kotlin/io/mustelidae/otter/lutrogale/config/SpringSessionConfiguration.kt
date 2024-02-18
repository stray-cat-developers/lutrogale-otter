package io.mustelidae.otter.lutrogale.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.session.JdbcSessionDataSourceScriptDatabaseInitializer
import org.springframework.boot.autoconfigure.session.JdbcSessionProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class SpringSessionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun jdbcSessionDataSourceInitializer(
        dataSource: DataSource,
        properties: JdbcSessionProperties,
    ): JdbcSessionDataSourceScriptDatabaseInitializer {
        return JdbcSessionDataSourceScriptDatabaseInitializer(dataSource, properties)
    }
}

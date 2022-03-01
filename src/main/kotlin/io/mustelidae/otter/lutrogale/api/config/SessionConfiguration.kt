package io.mustelidae.otter.lutrogale.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession


@Configuration
@EnableJdbcHttpSession
class SessionConfiguration {

}
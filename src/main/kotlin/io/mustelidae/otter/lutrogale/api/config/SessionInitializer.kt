package io.mustelidae.otter.lutrogale.api.config

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer

class SessionInitializer : AbstractHttpSessionApplicationInitializer(SessionConfiguration::class.java)

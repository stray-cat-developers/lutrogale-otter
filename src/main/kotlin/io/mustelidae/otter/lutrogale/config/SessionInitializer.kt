package io.mustelidae.otter.lutrogale.config

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer

class SessionInitializer : AbstractHttpSessionApplicationInitializer(SessionConfiguration::class.java)

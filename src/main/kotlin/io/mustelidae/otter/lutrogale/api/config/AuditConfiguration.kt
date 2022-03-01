package io.mustelidae.otter.lutrogale.api.config

import io.mustelidae.otter.lutrogale.web.commons.utils.RequestHelper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.Optional

@Configuration
@EnableJpaAuditing
class AuditConfiguration {

    @Bean
    internal fun auditorAware(): AuditorAware<*> {
        return AuditorAwareImpl()
    }
}

class AuditorAwareImpl : AuditorAware<String> {

    override fun getCurrentAuditor(): Optional<String> {
        var auditor = unknownAuditor

        if (RequestContextHolder.getRequestAttributes() != null) {
            val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request

            if (request.requestURI.startsWith("/api"))
                return Optional.of("api@osori.com")

            val osoriSessionInfo = RequestHelper.getSessionByAdmin(request)
            auditor = osoriSessionInfo.adminEmail
        }

        return Optional.of(auditor)
    }

    companion object {
        const val unknownAuditor = "S:unknown"
    }
}

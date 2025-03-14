package io.mustelidae.otter.lutrogale.config

import io.mustelidae.otter.lutrogale.api.permission.RoleHeader
import io.mustelidae.otter.lutrogale.web.AdminSession
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
        if (RequestContextHolder.getRequestAttributes() != null) {
            val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request

            val session = AdminSession(request.session)

            // Admin인 경우
            if (session.hasSession()) {
                return Optional.of("A:${session.getAdminId()}")
            }

            // API인 경우
            val id = request.getHeader(RoleHeader.XSystem.KEY)
            if (id.isNullOrBlank().not()) {
                return Optional.of("K:$id")
            }
        }

        return Optional.of("S:unknown")
    }
}

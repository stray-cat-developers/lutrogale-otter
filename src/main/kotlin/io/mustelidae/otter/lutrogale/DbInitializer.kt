package io.mustelidae.otter.lutrogale

import io.mustelidae.otter.lutrogale.web.domain.admin.Admin
import io.mustelidae.otter.lutrogale.web.domain.admin.repository.AdminRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.transaction.annotation.Transactional

@Profile("default")
@Configuration
@Transactional
class DbInitializer(
    private val adminRepository: AdminRepository
): CommandLineRunner {
    override fun run(vararg args: String?) {
        val osoriAdmin = Admin(
            "admin@osori.com",
            "슈퍼 관리자",
            "오소리의 모든 권한을 가지고 있습니다.",
            "/static/dist/img/osori.png"
        ).apply {
            setPassword("admin")
            status = true
        }
        adminRepository.save(osoriAdmin)
    }

}
package io.mustelidae.otter.lutrogale.config

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.http.server.PathContainer
import org.springframework.web.util.pattern.PathPatternParser

class AuditorAwareImplTest {

    @Test
    fun getCurrentAuditor() {
        // Given
        val parser = PathPatternParser().parse("/v*/verification/**")

        parser.matches(PathContainer.parsePath("/v1/verification/authorization-check/uri")) shouldBe true
        parser.matches(PathContainer.parsePath("/v1/maintain/authorization-check/uri")) shouldBe false
        parser.matches(PathContainer.parsePath("/maintain/authorization-check/uri")) shouldBe false
    }
}

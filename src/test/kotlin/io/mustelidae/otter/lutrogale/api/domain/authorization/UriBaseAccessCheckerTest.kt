package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources
import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationInteraction
import io.mustelidae.otter.lutrogale.web.domain.navigation.aFixture
import org.junit.jupiter.api.Test
import org.springframework.web.bind.annotation.RequestMethod

class UriBaseAccessCheckerTest {

    @Test
    fun validate() {
        check("/Targets/{id}/Summary/{sid}", "/Targets/34234/Summary/234") shouldBe true
        check("/Targets/*/Summary/{sid}", "/Targets/1/Summary/234") shouldBe true
        check("/Targets/*/Summary/*/test", "/Targets/1/Summary/detail/test") shouldBe true
        check("/Targets/**", "/Targets/plug23/Summary/234") shouldBe true
        check("/Tar?ets/Summary/test", "/TarTets/Summary/test") shouldBe true
        check("/Targets/{num:[0-9]+}/Summary/{sid}", "/Targets/1/Summary/234") shouldBe true

        check("/Targets/{id}/Summary/{sid}/test", "/Targets/34234/Summary/234") shouldBe false
        check("/Targets/{num:[0-9]+}/Summary/{sid}/test", "/Targets/abcd/Summary/234") shouldBe false
    }

    private fun check(patternUrl: String, targetUrl: String): Boolean {
        // Given
        val apiKey = "test"
        val menuNavigationInteraction: MenuNavigationInteraction = mockk()
        val menuNavigation = MenuNavigation.aFixture(apiKey)

        val menuNavigations = listOf(menuNavigation)
        every { menuNavigationInteraction.getFullUrl(menuNavigation) } returns patternUrl

        val accessGrant = AccessGrant(
            "test@osori.com",
            "test",
            Constant.AuthenticationCheckType.URI,
            accessUriGroup = listOf(
                AccessResources.AccessUri(targetUrl, RequestMethod.GET),
            ),
        )

        val checker = UriBaseAccessChecker(menuNavigationInteraction)
        val accesses = checker.validate(menuNavigations, accessGrant)

        return accesses.first().hasPermission
    }
}

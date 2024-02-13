package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import io.kotest.matchers.shouldBe
import io.mustelidae.otter.lutrogale.common.Constant
import org.junit.jupiter.api.Test
import org.springframework.web.bind.annotation.RequestMethod

internal class MenuTreeResourcesTest {

    @Test
    fun uriBlockNoPrefix() {
        // Given
        val request = MenuTreeResources.Request.Branch(
            "",
            "",
            "",
            "asdfasdf/adfasf/{fasdf}",
            Constant.NavigationType.MENU,
            RequestMethod.DELETE,
        )
        // When
        val refineBlock = request.getRefineUriBlock()
        // Then
        refineBlock shouldBe "/asdfasdf/adfasf/{fasdf}"
    }

    @Test
    fun uriBlockHasPostfix() {
        // Given
        val request = MenuTreeResources.Request.Branch(
            "",
            "",
            "",
            "asdfasdf/adfasf/{fasdf}/",
            Constant.NavigationType.MENU,
            RequestMethod.DELETE,
        )
        // When
        val refineBlock = request.getRefineUriBlock()
        // Then
        refineBlock shouldBe "/asdfasdf/adfasf/{fasdf}"
    }
}

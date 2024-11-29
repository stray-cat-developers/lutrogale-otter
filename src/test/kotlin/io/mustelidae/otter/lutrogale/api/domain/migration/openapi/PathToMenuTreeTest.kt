package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.kotest.matchers.shouldBe
import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.RequestMethod

class PathToMenuTreeTest {

    @Test
    fun makeCode () {
        val specs = listOf(
            HttpAPISpec("/sample/:id", "sample", listOf(RequestMethod.GET)),
            HttpAPISpec("/sample/hello/:id", "sample hello", listOf(RequestMethod.POST, RequestMethod.GET)),
            HttpAPISpec("/sample/hello/world/:id", "sample hello world", listOf(RequestMethod.POST, RequestMethod.GET)),
            HttpAPISpec("/never/:id", "never", listOf(RequestMethod.GET, RequestMethod.POST)),
            HttpAPISpec("/never/:id/die", "never die", listOf(RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT)),
        )

        val pathToMenuTree = PathToMenuTree(specs)

        val rootMenuNavigation = pathToMenuTree.make()

        rootMenuNavigation.menuNavigations.size shouldBe 3
    }
}
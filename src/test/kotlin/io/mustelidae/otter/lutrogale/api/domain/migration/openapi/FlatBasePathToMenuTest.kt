package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.web.bind.annotation.RequestMethod

class FlatBasePathToMenuTest {

    @Test
    fun makeCode() {
        val specs = listOf(
            HttpAPISpec("/sample/:id", "sample", listOf(RequestMethod.GET)),
            HttpAPISpec("/sample/hello/:id", "sample hello", listOf(RequestMethod.POST, RequestMethod.GET)),
            HttpAPISpec("/sample/hello/world/:id", "sample hello world", listOf(RequestMethod.POST, RequestMethod.GET)),
            HttpAPISpec("/never/:id", "never", listOf(RequestMethod.GET, RequestMethod.POST)),
            HttpAPISpec("/never/:id/die", "never die", listOf(RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT)),
            HttpAPISpec("/A/:a/B/C/D/:d", "ABCD", listOf(RequestMethod.POST)),
            HttpAPISpec("/A/:a/B/C/:c/D/:d", "ABCD", listOf(RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT)),
        )

        val pathToMenu: PathToMenu = FlatBasePathToMenu(specs)

        pathToMenu.makeTree()

        println(pathToMenu.printMenuTree())

        pathToMenu.rootMenuNavigation.menuNavigations.size shouldBe 14
    }
}

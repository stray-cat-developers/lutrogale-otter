package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import org.junit.jupiter.api.Test
import org.springframework.web.bind.annotation.RequestMethod

class TreeBasePathToMenuTest {

    @Test
    fun makeCode() {
        val specs = listOf(
            HttpAPISpec("/sample/:id", listOf(RequestMethod.GET), "sample"),
            HttpAPISpec("/sample/hello/:id", listOf(RequestMethod.POST, RequestMethod.GET), "sample hello"),
            HttpAPISpec("/sample/hello/world/:id", listOf(RequestMethod.POST, RequestMethod.GET), "sample hello world"),
            HttpAPISpec("/never/:id", listOf(RequestMethod.GET, RequestMethod.POST), "never"),
            HttpAPISpec("/never/:id/die", listOf(RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT), "never die"),
            HttpAPISpec("/slack/sample", listOf(RequestMethod.POST), "slack"),
            HttpAPISpec("/make/money/will/good", listOf(RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT), "ABCD"),
            HttpAPISpec("/make/nice", listOf(RequestMethod.POST), "POST"),
        )

        val root = MenuNavigation.root().apply {
            setBy(Project("migration", null, ""))
        }
        val treeBasePathToMenu = TreeBasePathToMenu(specs, root)
        val menuNavigationRepository: MenuNavigationRepository = mockk()

        every { menuNavigationRepository.save(any()) } returns MenuNavigation.root()

        treeBasePathToMenu.makeTree(menuNavigationRepository)

        println(treeBasePathToMenu.printMenuTree())

        treeBasePathToMenu.rootMenuNavigation.menuNavigations.size shouldBe 4
    }
}

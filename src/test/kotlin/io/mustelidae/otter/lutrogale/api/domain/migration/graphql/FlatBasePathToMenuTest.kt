package io.mustelidae.otter.lutrogale.api.domain.migration.graphql

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mustelidae.otter.lutrogale.api.domain.migration.client.DummyHttpSpecClient
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import org.junit.jupiter.api.Test
import org.springframework.web.bind.annotation.RequestMethod

class FlatBasePathToMenuTest {

    @Test
    fun graphQLTestGETAndPOST() {
        val schema = DummyHttpSpecClient.TWEET_GRAPHQL
        val rootMenuNavigation = MenuNavigation.root()
        val project = Project("gql test", null, "").apply {
            addBy(rootMenuNavigation)
        }
        val menuNavigationRepository: MenuNavigationRepository = mockk()
        every { menuNavigationRepository.save(any()) } returns MenuNavigation.root()
        val httpOperation = HttpOperation.GET_AND_POST

        val flatBasePathToMenu = FlatBasePathToMenu(project, schema, httpOperation)
        flatBasePathToMenu.makeTree(menuNavigationRepository)

        val menus = rootMenuNavigation.menuNavigations.groupBy { it.methodType }
        menus[RequestMethod.GET]!!.size shouldBe 6
        menus[RequestMethod.POST]!!.size shouldBe 3
    }

    @Test
    fun graphQLTestOnlyGET() {
        val schema = DummyHttpSpecClient.TWEET_GRAPHQL
        val rootMenuNavigation = MenuNavigation.root()
        val project = Project("gql test", null, "").apply {
            addBy(rootMenuNavigation)
        }
        val menuNavigationRepository: MenuNavigationRepository = mockk()
        every { menuNavigationRepository.save(any()) } returns MenuNavigation.root()
        val httpOperation = HttpOperation.ONLY_GET

        val flatBasePathToMenu = FlatBasePathToMenu(project, schema, httpOperation)
        flatBasePathToMenu.makeTree(menuNavigationRepository)

        val menus = rootMenuNavigation.menuNavigations.groupBy { it.methodType }
        menus[RequestMethod.GET]!!.size shouldBe 9
    }

    @Test
    fun graphQLTestOnlyPOST() {
        val schema = DummyHttpSpecClient.TWEET_GRAPHQL
        val rootMenuNavigation = MenuNavigation.root()
        val project = Project("gql test", null, "").apply {
            addBy(rootMenuNavigation)
        }
        val menuNavigationRepository: MenuNavigationRepository = mockk()
        every { menuNavigationRepository.save(any()) } returns MenuNavigation.root()
        val httpOperation = HttpOperation.ONLY_POST

        val flatBasePathToMenu = FlatBasePathToMenu(project, schema, httpOperation)
        flatBasePathToMenu.makeTree(menuNavigationRepository)

        val menus = rootMenuNavigation.menuNavigations.groupBy { it.methodType }
        menus[RequestMethod.POST]!!.size shouldBe 9
    }
}

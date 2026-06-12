package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation.ListStructure
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.bind.annotation.RequestMethod

class FlatBaseSyncToMenuTest {
    private lateinit var root: MenuNavigation
    private lateinit var project: Project
    private lateinit var repo: MenuNavigationRepository

    @BeforeEach
    fun setUp() {
        root = MenuNavigation.root()
        project = Project("test", null, "test-key", ListStructure.FLAT).apply { addBy(root) }
        repo = mockk()
        every { repo.save(any()) } returns MenuNavigation.root()
    }

    @Test
    fun `spec에 신규 API가 있으면 MenuNavigation이 추가된다`() {
        addExistingMenu("/users", RequestMethod.GET)

        val openAPI =
            buildOpenAPI(
                "/users" to listOf(RequestMethod.GET),
                "/orders" to listOf(RequestMethod.POST),
            )

        FlatBaseSyncToMenu(openAPI, root).makeTree(repo)

        val activeMenus = activeMenusOf(project)
        activeMenus.size shouldBe 2
        activeMenus.any { it.uriBlock == "/orders" && it.methodType == RequestMethod.POST } shouldBe true
    }

    @Test
    fun `spec에서 사라진 API의 MenuNavigation은 만료된다`() {
        addExistingMenu("/users", RequestMethod.GET)
        val toBeExpired = addExistingMenu("/orders", RequestMethod.POST)

        val openAPI =
            buildOpenAPI(
                "/users" to listOf(RequestMethod.GET),
            )

        FlatBaseSyncToMenu(openAPI, root).makeTree(repo)

        toBeExpired.status shouldBe false
        activeMenusOf(project).size shouldBe 1
    }

    @Test
    fun `이미 만료된 API가 spec에 다시 나타나면 새로 생성된다`() {
        addExistingMenu("/users", RequestMethod.GET)
        val expiredMenu = addExistingMenu("/orders", RequestMethod.GET).also { it.expire() }

        val openAPI =
            buildOpenAPI(
                "/users" to listOf(RequestMethod.GET),
                "/orders" to listOf(RequestMethod.GET),
            )

        FlatBaseSyncToMenu(openAPI, root).makeTree(repo)

        val activeMenus = activeMenusOf(project)
        activeMenus.size shouldBe 2
        activeMenus.any { it.uriBlock == "/orders" && it != expiredMenu } shouldBe true
        expiredMenu.status shouldBe false
    }

    @Test
    fun `spec과 기존 MenuNavigation이 동일하면 변경이 없다`() {
        addExistingMenu("/users", RequestMethod.GET)
        addExistingMenu("/orders", RequestMethod.POST)

        val openAPI =
            buildOpenAPI(
                "/users" to listOf(RequestMethod.GET),
                "/orders" to listOf(RequestMethod.POST),
            )

        FlatBaseSyncToMenu(openAPI, root).makeTree(repo)

        verify(exactly = 0) { repo.save(any()) }
        activeMenusOf(project).size shouldBe 2
    }

    @Test
    fun `같은 경로의 메서드 추가와 삭제는 독립적으로 처리된다`() {
        addExistingMenu("/users", RequestMethod.GET)
        val toBeExpired = addExistingMenu("/users", RequestMethod.POST)

        // POST 삭제, DELETE 추가
        val openAPI =
            buildOpenAPI(
                "/users" to listOf(RequestMethod.GET, RequestMethod.DELETE),
            )

        FlatBaseSyncToMenu(openAPI, root).makeTree(repo)

        toBeExpired.status shouldBe false
        val activeMenus = activeMenusOf(project)
        activeMenus.size shouldBe 2
        activeMenus.any { it.uriBlock == "/users" && it.methodType == RequestMethod.GET } shouldBe true
        activeMenus.any { it.uriBlock == "/users" && it.methodType == RequestMethod.DELETE } shouldBe true
    }

    @Test
    fun `기존 메뉴가 없는 상태에서 spec을 적용하면 모두 신규 생성된다`() {
        val openAPI =
            buildOpenAPI(
                "/users" to listOf(RequestMethod.GET, RequestMethod.POST),
                "/orders" to listOf(RequestMethod.GET),
            )

        FlatBaseSyncToMenu(openAPI, root).makeTree(repo)

        activeMenusOf(project).size shouldBe 3
    }

    @Test
    fun `spec이 빈 경우 기존 MenuNavigation이 모두 만료된다`() {
        val a = addExistingMenu("/users", RequestMethod.GET)
        val b = addExistingMenu("/orders", RequestMethod.POST)

        FlatBaseSyncToMenu(OpenAPI().paths(Paths()), root).makeTree(repo)

        a.status shouldBe false
        b.status shouldBe false
        activeMenusOf(project).size shouldBe 0
    }

    // --- helpers ---

    private fun addExistingMenu(
        uriBlock: String,
        method: RequestMethod,
    ): MenuNavigation {
        val seq = project.menuNavigations.size
        return MenuNavigation(
            "[$method] $uriBlock",
            Constant.NavigationType.FUNCTION,
            uriBlock,
            method,
            "j1_$seq",
            "1",
        ).also {
            it.setBy(project)
            it.setBy(root)
        }
    }

    private fun activeMenusOf(project: Project) = project.menuNavigations.filter { !it.isRoot() && it.status }

    private fun buildOpenAPI(vararg pathAndMethods: Pair<String, List<RequestMethod>>): OpenAPI {
        val paths = Paths()
        for ((url, methods) in pathAndMethods) {
            val pathItem = PathItem().summary(url)
            for (method in methods) {
                val op = Operation()
                when (method) {
                    RequestMethod.GET -> {
                        pathItem.get(op)
                    }

                    RequestMethod.POST -> {
                        pathItem.post(op)
                    }

                    RequestMethod.PUT -> {
                        pathItem.put(op)
                    }

                    RequestMethod.DELETE -> {
                        pathItem.delete(op)
                    }

                    RequestMethod.PATCH -> {
                        pathItem.patch(op)
                    }

                    else -> {}
                }
            }
            paths.addPathItem(url, pathItem)
        }
        return OpenAPI().paths(paths)
    }
}

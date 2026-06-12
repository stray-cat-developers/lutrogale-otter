package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

class TreeBaseSyncToMenuTest {
    private lateinit var root: MenuNavigation
    private lateinit var project: Project
    private lateinit var repo: MenuNavigationRepository

    @BeforeEach
    fun setUp() {
        root = MenuNavigation.root()
        project = Project("test", null, "test-key", ListStructure.TREE).apply { addBy(root) }
        repo = mockk()
        every { repo.save(any()) } returns MenuNavigation.root()
    }

    @Test
    fun `spec에 신규 경로가 추가되면 트리 노드가 생성된다`() {
        // 초기: GET /users
        setupTree(HttpAPISpec("/users", listOf(RequestMethod.GET), null))

        // 신규: GET /orders 추가
        val openAPI =
            buildOpenAPI(
                "/users" to listOf(RequestMethod.GET),
                "/orders" to listOf(RequestMethod.GET),
            )

        TreeBaseSyncToMenu(openAPI, root).makeTree(repo)

        val activeRootChildren = root.menuNavigations.filter { it.status }
        activeRootChildren.size shouldBe 2
        activeRootChildren.any { it.uriBlock == "/orders" && it.methodType == RequestMethod.GET } shouldBe true
    }

    @Test
    fun `spec에서 비-GET 메서드가 사라지면 해당 노드만 만료되고 GET 노드는 유지된다`() {
        // 초기: GET /users/:id, POST /users/:id
        setupTree(HttpAPISpec("/users/:id", listOf(RequestMethod.GET, RequestMethod.POST), null))

        // POST 제거
        val openAPI = buildOpenAPI("/users/:id" to listOf(RequestMethod.GET))

        TreeBaseSyncToMenu(openAPI, root).makeTree(repo)

        val usersNode = root.menuNavigations.first { it.uriBlock == "/users" && it.methodType == RequestMethod.GET }
        val idGetNode = usersNode.menuNavigations.firstOrNull { it.uriBlock == "/:id" && it.methodType == RequestMethod.GET }
        val idPostNode = usersNode.menuNavigations.firstOrNull { it.uriBlock == "/:id" && it.methodType == RequestMethod.POST }

        usersNode.status shouldBe true
        idGetNode?.status shouldBe true
        idPostNode?.status shouldBe false
    }

    @Test
    fun `spec에서 상위 경로가 사라지면 하위 노드 전체가 cascade 만료된다`() {
        // 초기: GET /users/profiles, GET /orders/items
        setupTree(
            HttpAPISpec("/users/profiles", listOf(RequestMethod.GET), null),
            HttpAPISpec("/orders/items", listOf(RequestMethod.GET), null),
        )

        // orders 전체 제거
        val openAPI = buildOpenAPI("/users/profiles" to listOf(RequestMethod.GET))

        TreeBaseSyncToMenu(openAPI, root).makeTree(repo)

        val ordersNode = root.menuNavigations.firstOrNull { it.uriBlock == "/orders" }
        ordersNode?.status shouldBe false
        ordersNode?.menuNavigations?.forEach { it.status shouldBe false }

        val usersNode = root.menuNavigations.first { it.uriBlock == "/users" }
        usersNode.status shouldBe true
    }

    @Test
    fun `spec이 줄어들어도 남아있는 경로의 노드는 유지된다`() {
        // 초기: GET /api/users/profiles, GET /api/users/settings
        setupTree(
            HttpAPISpec("/api/users/profiles", listOf(RequestMethod.GET), null),
            HttpAPISpec("/api/users/settings", listOf(RequestMethod.GET), null),
        )

        // settings 제거
        val openAPI = buildOpenAPI("/api/users/profiles" to listOf(RequestMethod.GET))

        TreeBaseSyncToMenu(openAPI, root).makeTree(repo)

        val apiNode = root.menuNavigations.first { it.uriBlock == "/api" }
        val usersNode = apiNode.menuNavigations.first { it.uriBlock == "/users" }
        val profilesNode = usersNode.menuNavigations.first { it.uriBlock == "/profiles" }
        val settingsNode = usersNode.menuNavigations.first { it.uriBlock == "/settings" }

        apiNode.status shouldBe true
        usersNode.status shouldBe true
        profilesNode.status shouldBe true
        settingsNode.status shouldBe false
    }

    @Test
    fun `spec이 전부 빈 경우 기존 트리 전체가 만료된다`() {
        setupTree(
            HttpAPISpec("/users/profiles", listOf(RequestMethod.GET), null),
            HttpAPISpec("/orders", listOf(RequestMethod.GET, RequestMethod.POST), null),
        )

        TreeBaseSyncToMenu(OpenAPI().paths(Paths()), root).makeTree(repo)

        root.menuNavigations.filter { it.status }.size shouldBe 0
    }

    @Test
    fun `이미 만료된 노드는 재만료 처리 없이 건너뛴다`() {
        setupTree(HttpAPISpec("/users", listOf(RequestMethod.GET), null))
        val usersNode = root.menuNavigations.first { it.uriBlock == "/users" }

        val expiredNode =
            MenuNavigation(
                "legacy",
                io.mustelidae.otter.lutrogale.common.Constant.NavigationType.CATEGORY,
                "/legacy",
                RequestMethod.GET,
                "j1_99",
                "1",
            ).also {
                it.setBy(project)
                it.setBy(root)
                it.expire()
            }

        val openAPI = buildOpenAPI("/users" to listOf(RequestMethod.GET))

        TreeBaseSyncToMenu(openAPI, root).makeTree(repo)

        usersNode.status shouldBe true
        expiredNode.status shouldBe false
        verify(exactly = 0) { repo.save(expiredNode) }
    }

    @Test
    fun `expire된 경로가 spec에 다시 나타나면 신규 노드로 생성된다`() {
        setupTree(HttpAPISpec("/users", listOf(RequestMethod.GET), null))
        val expiredUsersNode = root.menuNavigations.first { it.uriBlock == "/users" }
        expiredUsersNode.expire()

        val openAPI = buildOpenAPI("/users" to listOf(RequestMethod.GET))
        TreeBaseSyncToMenu(openAPI, root).makeTree(repo)

        val activeChildren = root.menuNavigations.filter { it.status }
        activeChildren.size shouldBe 1
        activeChildren.none { it === expiredUsersNode } shouldBe true
        expiredUsersNode.status shouldBe false
    }

    @Test
    fun `spec과 기존 트리가 동일하면 변경이 없다`() {
        val openAPI = buildOpenAPI("/users" to listOf(RequestMethod.GET, RequestMethod.POST))

        // 첫 번째 sync로 초기 트리 생성
        TreeBaseSyncToMenu(openAPI, root).makeTree(repo)
        io.mockk.clearMocks(repo, answers = false, recordedCalls = true)
        every { repo.save(any()) } returns MenuNavigation.root()

        // 동일한 spec으로 재실행 — save 호출 없어야 함
        TreeBaseSyncToMenu(openAPI, root).makeTree(repo)

        verify(exactly = 0) { repo.save(any()) }
    }

    @Test
    fun `기존 트리가 없는 상태에서 spec을 적용하면 트리가 신규 생성된다`() {
        val openAPI =
            buildOpenAPI(
                "/users/:id" to listOf(RequestMethod.GET, RequestMethod.POST),
                "/orders" to listOf(RequestMethod.GET),
            )

        TreeBaseSyncToMenu(openAPI, root).makeTree(repo)

        // /users(GET), /orders(GET) 두 개가 root 자식으로 생성
        val rootChildren = root.menuNavigations.filter { it.status }
        rootChildren.size shouldBe 2
        rootChildren.any { it.uriBlock == "/users" } shouldBe true
        rootChildren.any { it.uriBlock == "/orders" } shouldBe true
    }

    // --- helpers ---

    private fun setupTree(vararg specs: HttpAPISpec) {
        TreeBasePathToMenu(specs.toList(), root).makeTree(repo)
    }

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

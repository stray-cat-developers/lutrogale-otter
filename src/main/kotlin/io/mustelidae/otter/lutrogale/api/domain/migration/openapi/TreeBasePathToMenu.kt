package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.swagger.v3.oas.models.OpenAPI
import org.springframework.web.bind.annotation.RequestMethod
import java.util.concurrent.atomic.AtomicInteger

class TreeBasePathToMenu : PathToMenu {
    constructor(openApi: OpenAPI) {
        this.pathWithHttpMethods = PathCollector(openApi).collectPathAndMethods()
    }

    constructor(httpApiSpecs: List<HttpAPISpec>) {
        this.pathWithHttpMethods = httpApiSpecs
    }

    private var pathWithHttpMethods: List<HttpAPISpec>

    private val atomicInt = AtomicInteger(1)

    override var rootMenuNavigation = MenuNavigation.root()

    override fun makeTree() {
        val sortedPaths = pathWithHttpMethods.sortedBy { it.url }

        for (apiSpec in sortedPaths) {
            addTree(rootMenuNavigation, apiSpec)
        }
    }

    override fun printMenuTree(): String {
        val prettyPrint = StringBuilder()
        printTree(prettyPrint, rootMenuNavigation, 0)
        return prettyPrint.toString()
    }

    private fun printTree(print: StringBuilder, menuNavigation: MenuNavigation, depth: Int) {
        if (menuNavigation.uriBlock != "/") {
            print.append("${"    ".repeat(depth)}- ${menuNavigation.uriBlock} ${menuNavigation.methodType}\n")
        }
        menuNavigation.menuNavigations.forEach { printTree(print, it, depth + 1) }
    }

    private fun addTree(parentMenuNavigation: MenuNavigation, apiSpec: HttpAPISpec) {
        val urlParts = apiSpec.getUrlParts()
        var current = parentMenuNavigation

        for (part in urlParts) {
            val child = current.menuNavigations.find { it.uriBlock == part && it.methodType == RequestMethod.GET }
                ?: MenuNavigation(
                    apiSpec.summary,
                    Constant.NavigationType.MENU,
                    part,
                    RequestMethod.GET,
                    atomicInt.get().toString(),
                    current.treeId,
                ).also {
                    current.menuNavigations.add(it)
                }
            current = child
        }

        // 각 메서드별로 자식 노드를 추가
        apiSpec.methods.forEach { method ->
            current.menuNavigations.find { it.uriBlock == current.uriBlock && it.methodType == method }
                ?: current.menuNavigations.add(
                    MenuNavigation(
                        apiSpec.summary,
                        Constant.NavigationType.MENU,
                        current.uriBlock,
                        method,
                        atomicInt.get().toString(),
                        current.treeId,
                    ),
                )
        }
    }
}

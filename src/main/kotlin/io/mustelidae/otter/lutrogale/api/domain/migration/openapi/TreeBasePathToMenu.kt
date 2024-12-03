package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.mustelidae.otter.lutrogale.api.domain.migration.PathToMenu
import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.swagger.v3.oas.models.OpenAPI
import org.springframework.web.bind.annotation.RequestMethod
import java.util.concurrent.atomic.AtomicInteger

class TreeBasePathToMenu : PathToMenu {
    constructor(openApi: OpenAPI, rootMenuNavigation: MenuNavigation) {
        this.pathWithHttpMethods = PathCollector(openApi).collectPathAndMethods()
        this.rootMenuNavigation = rootMenuNavigation
    }

    constructor(httpApiSpecs: List<HttpAPISpec>, rootMenuNavigation: MenuNavigation) {
        this.pathWithHttpMethods = httpApiSpecs
        this.rootMenuNavigation = rootMenuNavigation
    }

    private var pathWithHttpMethods: List<HttpAPISpec>

    private val atomicInt = AtomicInteger(1)

    override var rootMenuNavigation: MenuNavigation

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

        for ((index, part) in urlParts.withIndex()) {
            var child = current.menuNavigations.find { it.uriBlock == part && it.methodType == RequestMethod.GET }

            if (child == null) {
                val type = when (index) {
                    0 -> Constant.NavigationType.CATEGORY
                    1 -> Constant.NavigationType.MENU
                    else -> Constant.NavigationType.FUNCTION
                }

                val new = MenuNavigation(
                    apiSpec.summary ?: "[GET] ${apiSpec.url.replace("/", " ")}",
                    type,
                    part,
                    RequestMethod.GET,
                    atomicInt.get().toString(),
                    current.treeId,
                )
                current.menuNavigations.add(new)
                child = new
            }

            if (index == urlParts.lastIndex) {
                for (method in apiSpec.methods) {
                    val sameMenu = current.menuNavigations.find { it.uriBlock == part && it.methodType == method }

                    if (sameMenu == null) {
                        current.menuNavigations.add(
                            MenuNavigation(
                                apiSpec.summary ?: "[$method] ${apiSpec.url.replace("/", " ")}",
                                Constant.NavigationType.FUNCTION,
                                part,
                                method,
                                atomicInt.get().toString(),
                                current.treeId,
                            ),
                        )
                    }
                }
            }
            current = child
        }
    }
}

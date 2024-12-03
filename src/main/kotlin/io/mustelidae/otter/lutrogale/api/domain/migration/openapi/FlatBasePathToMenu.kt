package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.mustelidae.otter.lutrogale.api.domain.migration.PathToMenu
import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.swagger.v3.oas.models.OpenAPI
import java.util.concurrent.atomic.AtomicInteger

class FlatBasePathToMenu : PathToMenu {

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
            for (method in apiSpec.methods) {
                rootMenuNavigation.addBy(
                    MenuNavigation(
                        apiSpec.summary ?: "[$method] ${apiSpec.url.replace("/", " ")}",
                        Constant.NavigationType.FUNCTION,
                        apiSpec.url,
                        method,
                        atomicInt.get().toString(),
                        rootMenuNavigation.treeId,
                    ),
                )
            }
        }
    }

    override fun printMenuTree(): String {
        val prettyPrint = StringBuilder()

        rootMenuNavigation.menuNavigations.forEach {
            prettyPrint.append("${it.uriBlock} ${it.methodType}\n")
        }

        return prettyPrint.toString()
    }
}

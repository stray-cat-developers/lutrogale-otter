package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.mustelidae.otter.lutrogale.api.domain.migration.PathToMenu
import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.swagger.v3.oas.models.OpenAPI
import java.util.concurrent.atomic.AtomicInteger

class FlatBasePathToMenu : PathToMenu {

    constructor(openApi: OpenAPI, rootMenuNavigation: MenuNavigation) {
        this.pathWithHttpMethods = PathCollector(openApi).collectPathAndMethods()
        this.rootMenuNavigation = rootMenuNavigation
        this.project = rootMenuNavigation.project!!
    }

    constructor(httpApiSpecs: List<HttpAPISpec>, rootMenuNavigation: MenuNavigation) {
        this.pathWithHttpMethods = httpApiSpecs
        this.rootMenuNavigation = rootMenuNavigation
        this.project = rootMenuNavigation.project!!
    }

    private var pathWithHttpMethods: List<HttpAPISpec>
    private val project: Project

    override var rootMenuNavigation: MenuNavigation

    override fun makeTree(menuNavigationRepository: MenuNavigationRepository) {
        val atomicInt = AtomicInteger(1)
        val sortedPaths = pathWithHttpMethods.sortedBy { it.url }

        for (apiSpec in sortedPaths) {
            for (method in apiSpec.methods) {
                val newMenu = MenuNavigation(
                    apiSpec.summary ?: "[$method] ${transformName(apiSpec.url)}",
                    Constant.NavigationType.FUNCTION,
                    apiSpec.url,
                    method,
                    "j${rootMenuNavigation.treeId}_${atomicInt.getAndIncrement()}",
                    rootMenuNavigation.treeId,
                ).also {
                    it.setBy(project)
                    it.setBy(rootMenuNavigation)
                }

                menuNavigationRepository.save(newMenu)
                rootMenuNavigation.addBy(newMenu)
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

package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.mustelidae.otter.lutrogale.api.domain.migration.PathToMenu
import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.swagger.v3.oas.models.OpenAPI
import org.springframework.web.bind.annotation.RequestMethod
import java.util.concurrent.atomic.AtomicInteger

/**
 * TreeBasePathToMenu 클래스는 웹 API를 기반으로 메뉴 트리를 구성합니다.
 */
class TreeBasePathToMenu : PathToMenu {
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
    override var rootMenuNavigation: MenuNavigation

    private val project: Project
    private val atomicInt = AtomicInteger(1)

    /**
     * 수집된 API 경로와 메서드 정보를 기반으로 메뉴 트리를 생성합니다.
     * 입력된 메서드와 경로로 불러온 메뉴를 찾고, 없으면 새로운 메뉴를 생성하여 MenuNavigationRepository에 저장합니다.
     */
    override fun makeTree(menuNavigationRepository: MenuNavigationRepository) {
        val sortedPaths = pathWithHttpMethods.sortedBy { it.url }

        for (apiSpec in sortedPaths) {
            addTree(rootMenuNavigation, apiSpec, menuNavigationRepository)
        }
    }

    private fun addTree(
        parentMenuNavigation: MenuNavigation,
        apiSpec: HttpAPISpec,
        menuNavigationRepository: MenuNavigationRepository,
    ) {
        val urlParts = apiSpec.getUrlParts()
        var current = parentMenuNavigation

        for ((index, part) in urlParts.withIndex()) {
            var child = current.menuNavigations.find { it.uriBlock == "/$part" && it.methodType == RequestMethod.GET }

            if (child == null) {
                val type = when (index) {
                    0 -> Constant.NavigationType.CATEGORY
                    1 -> Constant.NavigationType.MENU
                    else -> Constant.NavigationType.FUNCTION
                }

                val name = if (index == 0) apiSpec.summary ?: "[GET] ${transformName(part)}" else "[GET] ${transformName(part)}"

                val newMenu = MenuNavigation(
                    name,
                    type,
                    "/$part",
                    RequestMethod.GET,
                    "j${rootMenuNavigation.treeId}_${atomicInt.getAndIncrement()}",
                    current.treeId,
                ).also {
                    it.setBy(project)
                }

                menuNavigationRepository.save(newMenu)
                current.addBy(newMenu)

                child = newMenu
            }

            if (index == urlParts.lastIndex) {
                for (method in apiSpec.methods) {
                    if (method == RequestMethod.GET) continue

                    val sameMenu = current.menuNavigations.find { it.uriBlock == part && it.methodType == method }

                    if (sameMenu == null) {
                        val newMenu = MenuNavigation(
                            "[$method] ${transformName(part)}",
                            Constant.NavigationType.FUNCTION,
                            "/$part",
                            method,
                            "j${rootMenuNavigation.treeId}_${atomicInt.getAndIncrement()}",
                            current.treeId,
                        ).also {
                            it.setBy(project)
                        }

                        menuNavigationRepository.save(newMenu)
                        current.addBy(newMenu)
                    }
                }
            }

            current = child
        }
    }

    override fun printMenuTree(): String {
        val prettyPrint = StringBuilder()
        printTree(prettyPrint, rootMenuNavigation, 0)
        return prettyPrint.toString()
    }

    private fun printTree(print: StringBuilder, menuNavigation: MenuNavigation, depth: Int) {
        if (menuNavigation.uriBlock != "/") {
            print.append("${"    ".repeat(depth)}- ${menuNavigation.uriBlock} (${menuNavigation.methodType})\n")
        }
        menuNavigation.menuNavigations.forEach { printTree(print, it, depth + 1) }
    }
}

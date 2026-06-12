package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.mustelidae.otter.lutrogale.api.domain.migration.PathToMenu
import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.swagger.v3.oas.models.OpenAPI
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMethod
import java.util.concurrent.atomic.AtomicInteger

class TreeBaseSyncToMenu(
    openAPI: OpenAPI,
    override var rootMenuNavigation: MenuNavigation,
) : PathToMenu {
    private val log = LoggerFactory.getLogger(javaClass)
    private val pathWithHttpMethods: List<HttpAPISpec> = PathCollector(openAPI).collectPathAndMethods()
    private val project: Project = rootMenuNavigation.project!!
    private val atomicInt = AtomicInteger(1)

    override fun makeTree(menuNavigationRepository: MenuNavigationRepository) {
        val maxSeq =
            project.menuNavigations
                .mapNotNull { menu ->
                    menu.treeId
                        .takeIf { it.startsWith("j${rootMenuNavigation.treeId}_") }
                        ?.substringAfter("j${rootMenuNavigation.treeId}_")
                        ?.toIntOrNull()
                }.maxOrNull() ?: 0
        atomicInt.set(maxSeq + 1)

        val sortedPaths = pathWithHttpMethods.sortedBy { it.url }

        for (apiSpec in sortedPaths) {
            addTree(rootMenuNavigation, apiSpec, menuNavigationRepository)
        }

        expireRemovedFromTree(rootMenuNavigation, sortedPaths, 0, menuNavigationRepository)
    }

    private fun addTree(
        parentMenuNavigation: MenuNavigation,
        apiSpec: HttpAPISpec,
        menuNavigationRepository: MenuNavigationRepository,
    ) {
        val urlParts = apiSpec.getUrlParts()
        var current = parentMenuNavigation

        for ((index, part) in urlParts.withIndex()) {
            var child =
                current.menuNavigations.find {
                    it.uriBlock == "/$part" && it.methodType == RequestMethod.GET && it.status
                }

            if (child == null) {
                val type =
                    when (index) {
                        0 -> Constant.NavigationType.CATEGORY
                        1 -> Constant.NavigationType.MENU
                        else -> Constant.NavigationType.FUNCTION
                    }
                val name = if (index == 0) apiSpec.summary ?: "[GET] ${transformName(part)}" else "[GET] ${transformName(part)}"
                val newMenu =
                    MenuNavigation(
                        name,
                        type,
                        "/$part",
                        RequestMethod.GET,
                        "j${rootMenuNavigation.treeId}_${atomicInt.getAndIncrement()}",
                        current.treeId,
                    ).also { it.setBy(project) }
                menuNavigationRepository.save(newMenu)
                current.addBy(newMenu)
                child = newMenu
            }

            if (index == urlParts.lastIndex) {
                for (method in apiSpec.methods) {
                    if (method == RequestMethod.GET) continue
                    val exists = current.menuNavigations.any { it.uriBlock == "/$part" && it.methodType == method && it.status }
                    if (!exists) {
                        val newMenu =
                            MenuNavigation(
                                "[$method] ${transformName(part)}",
                                Constant.NavigationType.FUNCTION,
                                "/$part",
                                method,
                                "j${rootMenuNavigation.treeId}_${atomicInt.getAndIncrement()}",
                                current.treeId,
                            ).also { it.setBy(project) }
                        menuNavigationRepository.save(newMenu)
                        current.addBy(newMenu)
                    }
                }
            }

            current = child
        }
    }

    /**
     * 재귀적으로 기존 트리를 순회하면서 새 스펙에 없는 노드를 만료시킨다.
     * GET 노드(경로 세그먼트)가 새 스펙에 없으면 해당 노드와 모든 하위 노드를 만료한다.
     */
    private fun expireRemovedFromTree(
        node: MenuNavigation,
        activeSpecs: List<HttpAPISpec>,
        depth: Int,
        menuNavigationRepository: MenuNavigationRepository,
    ) {
        for (child in node.menuNavigations.toList()) {
            if (!child.status) continue

            val segment = child.uriBlock.removePrefix("/")
            val specsWithSegment =
                activeSpecs.filter { spec ->
                    spec.getUrlParts().getOrNull(depth) == segment
                }

            if (child.methodType == RequestMethod.GET) {
                if (specsWithSegment.isEmpty()) {
                    expireSubtree(child, menuNavigationRepository)
                } else {
                    expireRemovedFromTree(child, specsWithSegment, depth + 1, menuNavigationRepository)
                }
            } else {
                // 비-GET 메서드 노드: 같은 경로 세그먼트에서 이 메서드가 여전히 존재하는지 확인
                val hasMethod = specsWithSegment.any { child.methodType in it.methods }
                if (!hasMethod) {
                    child.expire()
                    menuNavigationRepository.save(child)
                    log.info("Sync: expired ${child.uriBlock} [${child.methodType}]")
                }
            }
        }
    }

    private fun expireSubtree(
        node: MenuNavigation,
        menuNavigationRepository: MenuNavigationRepository,
    ) {
        for (child in node.menuNavigations.toList()) {
            if (child.status) expireSubtree(child, menuNavigationRepository)
        }
        node.expire()
        menuNavigationRepository.save(node)
        log.info("Sync: expired subtree node ${node.uriBlock} [${node.methodType}]")
    }

    override fun printMenuTree(): String {
        val sb = StringBuilder()
        printTree(sb, rootMenuNavigation, 0)
        return sb.toString()
    }

    private fun printTree(
        print: StringBuilder,
        menuNavigation: MenuNavigation,
        depth: Int,
    ) {
        if (menuNavigation.uriBlock != "/") {
            print.append("${"    ".repeat(depth)}- ${menuNavigation.uriBlock} (${menuNavigation.methodType})\n")
        }
        menuNavigation.menuNavigations.forEach { printTree(print, it, depth + 1) }
    }
}

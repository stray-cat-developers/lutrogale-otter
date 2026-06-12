package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.mustelidae.otter.lutrogale.api.domain.migration.PathToMenu
import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.swagger.v3.oas.models.OpenAPI
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

class FlatBaseSyncToMenu(
    openAPI: OpenAPI,
    override var rootMenuNavigation: MenuNavigation,
) : PathToMenu {
    private val log = LoggerFactory.getLogger(javaClass)
    private val pathWithHttpMethods: List<HttpAPISpec> = PathCollector(openAPI).collectPathAndMethods()
    private val project: Project = rootMenuNavigation.project!!

    override fun makeTree(menuNavigationRepository: MenuNavigationRepository) {
        val existingMenus = project.menuNavigations.filter { !it.isRoot() && it.status }
        val existingMenuKeys = existingMenus.map { "${it.methodType}_${it.uriBlock}" }.toSet()

        val specMenuKeys =
            pathWithHttpMethods
                .flatMap { spec ->
                    spec.methods.map { method -> "${method}_${spec.url}" }
                }.toSet()

        val maxSeq =
            existingMenus
                .mapNotNull { menu ->
                    menu.treeId
                        .takeIf { it.startsWith("j${rootMenuNavigation.treeId}_") }
                        ?.substringAfter("j${rootMenuNavigation.treeId}_")
                        ?.toIntOrNull()
                }.maxOrNull() ?: 0
        val atomicInt = AtomicInteger(maxSeq + 1)

        pathWithHttpMethods.forEach { spec ->
            spec.methods.forEach { method ->
                if ("${method}_${spec.url}" !in existingMenuKeys) {
                    val newMenu =
                        MenuNavigation(
                            spec.summary ?: "[$method] ${transformName(spec.url)}",
                            Constant.NavigationType.FUNCTION,
                            spec.url,
                            method,
                            "j${rootMenuNavigation.treeId}_${atomicInt.getAndIncrement()}",
                            rootMenuNavigation.treeId,
                        ).also {
                            it.setBy(project)
                            it.setBy(rootMenuNavigation)
                        }
                    menuNavigationRepository.save(newMenu)
                    log.info("Sync: added ${spec.url} [$method]")
                }
            }
        }

        existingMenus
            .filter { "${it.methodType}_${it.uriBlock}" !in specMenuKeys }
            .forEach { menu ->
                menu.expire()
                menuNavigationRepository.save(menu)
                log.info("Sync: expired ${menu.uriBlock} [${menu.methodType}]")
            }
    }

    override fun printMenuTree(): String {
        val sb = StringBuilder()
        rootMenuNavigation.menuNavigations.forEach { sb.append("${it.uriBlock} ${it.methodType}\n") }
        return sb.toString()
    }
}

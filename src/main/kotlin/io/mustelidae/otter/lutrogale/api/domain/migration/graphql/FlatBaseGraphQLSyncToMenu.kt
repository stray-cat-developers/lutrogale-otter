package io.mustelidae.otter.lutrogale.api.domain.migration.graphql

import graphql.language.ObjectTypeDefinition
import graphql.schema.idl.SchemaParser
import io.mustelidae.otter.lutrogale.api.domain.migration.PathToMenu
import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMethod
import java.util.concurrent.atomic.AtomicInteger

class FlatBaseGraphQLSyncToMenu(
    val project: Project,
    private val scheme: String,
) : PathToMenu {
    private val log = LoggerFactory.getLogger(javaClass)

    override var rootMenuNavigation: MenuNavigation = project.menuNavigations.first()

    override fun makeTree(menuNavigationRepository: MenuNavigationRepository) {
        try {
            log.info("Starting GraphQL sync for project: ${project.name}")

            val schemeFields = extractFieldsFromScheme()

            val existingMenus = project.menuNavigations.filter { !it.isRoot() && it.status }
            val existingMenuKeys = existingMenus.map { "${it.methodType}_${it.uriBlock}" }.toSet()

            val newFields = schemeFields.filter { "${it.method}_${it.fieldName}" !in existingMenuKeys }
            newFields.forEach { fieldInfo ->
                addNewMenuNavigation(fieldInfo, menuNavigationRepository)
            }

            val schemeMenuKeys = schemeFields.map { "${it.method}_${it.fieldName}" }.toSet()
            val menusToExpire = existingMenus.filter { "${it.methodType}_${it.uriBlock}" !in schemeMenuKeys }
            menusToExpire.forEach { menu ->
                log.info("Expiring menu: ${menu.name} as it's not found in scheme")
                menu.expire()
                menuNavigationRepository.save(menu)
            }

            log.info("Successfully synced project: ${project.name}, added ${newFields.size} menus, expired ${menusToExpire.size} menus")
        } catch (e: Exception) {
            log.debug("Exception details for sync failure in project ${project.name}: ${e.message}", e)
            log.error("Failed to sync project: ${project.name}, error: ${e.message}", e)
            throw e
        }
    }

    private fun extractFieldsFromScheme(): List<FieldInfo> {
        val fieldInfos = mutableListOf<FieldInfo>()

        val existingTreeIds =
            project.menuNavigations
                .mapNotNull { menu ->
                    val treeId = menu.treeId
                    if (treeId.startsWith("j${rootMenuNavigation.treeId}_")) {
                        treeId.substringAfter("j${rootMenuNavigation.treeId}_").toIntOrNull()
                    } else {
                        null
                    }
                }

        val startNumber =
            if (existingTreeIds.isNotEmpty()) {
                existingTreeIds.maxOf { it } + 1
            } else {
                1
            }

        val atomicInt = AtomicInteger(startNumber)

        try {
            val typeDefinitionRegistry = SchemaParser().parse(scheme)
            val types = typeDefinitionRegistry.types()

            val query = types["Query"] as? ObjectTypeDefinition
            query?.fieldDefinitions?.forEach { field ->
                fieldInfos.add(
                    FieldInfo(
                        name = field.description?.content ?: "[${RequestMethod.GET}] ${transformName(field.name)}",
                        fieldName = field.name,
                        type = "Query",
                        method = RequestMethod.GET,
                        treeId = "j${rootMenuNavigation.treeId}_${atomicInt.getAndIncrement()}",
                    ),
                )
            }

            val mutation = types["Mutation"] as? ObjectTypeDefinition
            mutation?.fieldDefinitions?.forEach { field ->
                fieldInfos.add(
                    FieldInfo(
                        name = field.description?.content ?: "[${RequestMethod.POST}] ${transformName(field.name)}",
                        fieldName = field.name,
                        type = "Mutation",
                        method = RequestMethod.POST,
                        treeId = "j${rootMenuNavigation.treeId}_${atomicInt.getAndIncrement()}",
                    ),
                )
            }
        } catch (e: Exception) {
            log.error("Failed to parse GraphQL scheme: ${e.message}", e)
            throw e
        }

        return fieldInfos
    }

    private fun addNewMenuNavigation(
        fieldInfo: FieldInfo,
        menuNavigationRepository: MenuNavigationRepository,
    ) {
        val menuNavigation =
            MenuNavigation(
                name = fieldInfo.name,
                type = Constant.NavigationType.FUNCTION,
                uriBlock = fieldInfo.fieldName,
                methodType = fieldInfo.method,
                treeId = fieldInfo.treeId,
                parentTreeId = rootMenuNavigation.treeId,
            ).also {
                it.setBy(project)
                it.setBy(rootMenuNavigation)
            }

        menuNavigationRepository.save(menuNavigation)
        log.info("Added new menu navigation: ${fieldInfo.name}")
    }

    override fun printMenuTree(): String {
        val prettyPrint = StringBuilder()

        rootMenuNavigation.menuNavigations.forEach {
            prettyPrint.append("${it.uriBlock} ${it.methodType}\n")
        }

        return prettyPrint.toString()
    }

    private data class FieldInfo(
        val name: String,
        val fieldName: String,
        val type: String,
        val method: RequestMethod,
        val treeId: String,
    )
}

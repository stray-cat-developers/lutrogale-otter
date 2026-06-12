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

/**
 * GraphQL 스키마를 동기화하여 메뉴 구조로 변환하는 클래스입니다.
 * 기존 메뉴와 새로운 스키마를 비교하여 추가/만료 처리를 수행합니다.
 *
 * @property project 메뉴 네비게이션과 연관된 프로젝트입니다.
 * @property scheme GraphQL 스키마 문자열입니다.
 */
class FlatBaseGraphQLSyncToMenu(
    val project: Project,
    private val scheme: String,
) : PathToMenu {
    private val log = LoggerFactory.getLogger(javaClass)

    override var rootMenuNavigation: MenuNavigation = project.menuNavigations.first()

    /**
     * GraphQL 스키마를 분석하여 기존 메뉴와 동기화합니다.
     * 새로운 필드는 추가하고, 스키마에 없는 기존 메뉴는 만료 처리합니다.
     *
     * @param menuNavigationRepository MenuNavigation 엔티티들과 상호작용하기 위해 사용되는 저장소
     */
    override fun makeTree(menuNavigationRepository: MenuNavigationRepository) {
        try {
            log.info("Starting GraphQL sync for project: ${project.name}")

            // 1. scheme에서 필드 정보 추출
            val schemeFields = extractFieldsFromScheme()

            // 2. 기존 메뉴 네비게이션 리스트 (root 제외)
            val existingMenus = project.menuNavigations.filter { !it.isRoot() && it.status }
            val existingMenuKeys = existingMenus.map { "${it.methodType}_${it.uriBlock}" }.toSet()

            // 3. 기존 메뉴 네비게이션 리스트에 없는 scheme를 찾아 신규 추가
            val newFields = schemeFields.filter { "${it.method}_${it.fieldName}" !in existingMenuKeys }
            newFields.forEach { fieldInfo ->
                addNewMenuNavigation(fieldInfo, menuNavigationRepository)
            }

            // 4. 기존 메뉴 네비게이션 리스트에는 있는데 scheme가 없는 경우 해당 메뉴 네비게이션 expire
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

        // 기존 메뉴 네비게이션의 treeId에서 마지막 번호 추출
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

            // Query 필드 처리
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

            // Mutation 필드 처리
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

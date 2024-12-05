package io.mustelidae.otter.lutrogale.api.domain.migration.graphql

import graphql.language.ObjectTypeDefinition
import graphql.schema.idl.SchemaParser
import io.mustelidae.otter.lutrogale.api.domain.migration.PathToMenu
import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import org.springframework.web.bind.annotation.RequestMethod
import java.util.concurrent.atomic.AtomicInteger

/**
 * 평평한 기본 경로를 메뉴 구조로 변환하는 클래스를 나타냅니다.
 *
 * @property project 메뉴 내비게이션과 연관된 프로젝트입니다.
 * @property scheme 파싱에 사용되는 스키마입니다.
 * @property httpOperation HTTP 작업 타입입니다.
 */
class FlatBasePathToMenu(
    val project: Project,
    private val scheme: String,
    private val httpOperation: HttpOperation,
) : PathToMenu {
    override var rootMenuNavigation: MenuNavigation = project.menuNavigations.first()

    /**
     * 주어진 Query와 Mutation 타입의 필드 정의를 처리하여 트리 구조를 생성합니다.
     *
     * @param menuNavigationRepository MenuNavigation 엔티티들과 상호작용하기 위해 사용되는 저장소
     */
    override fun makeTree(menuNavigationRepository: MenuNavigationRepository) {
        val atomicInt = AtomicInteger(1)
        val typeDefinitionRegistry = SchemaParser().parse(scheme)
        val type = typeDefinitionRegistry.types()

        val query = type["Query"] as ObjectTypeDefinition
        val queryMethod = when (httpOperation) {
            HttpOperation.ONLY_GET -> RequestMethod.GET
            HttpOperation.ONLY_POST -> RequestMethod.POST
            HttpOperation.GET_AND_POST -> RequestMethod.GET
        }

        processFieldDefinitions(query, queryMethod, atomicInt, menuNavigationRepository)

        val mutation = type["Mutation"] as ObjectTypeDefinition
        val mutationMethod = when (httpOperation) {
            HttpOperation.ONLY_GET -> RequestMethod.GET
            HttpOperation.ONLY_POST -> RequestMethod.POST
            HttpOperation.GET_AND_POST -> RequestMethod.POST
        }

        processFieldDefinitions(mutation, mutationMethod, atomicInt, menuNavigationRepository)
    }

    private fun processFieldDefinitions(
        query: ObjectTypeDefinition,
        method: RequestMethod,
        atomicInt: AtomicInteger,
        menuNavigationRepository: MenuNavigationRepository,
    ) {
        for (field in query.fieldDefinitions) {
            val newMenu = MenuNavigation(
                field.description?.content ?: "[$method] ${transformName(field.name)}",
                Constant.NavigationType.FUNCTION,
                field.name,
                method,
                "j${rootMenuNavigation.treeId}_${atomicInt.getAndIncrement()}",
                rootMenuNavigation.treeId,
            ).also {
                it.setBy(project)
                it.setBy(rootMenuNavigation)
            }

            menuNavigationRepository.save(newMenu)
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

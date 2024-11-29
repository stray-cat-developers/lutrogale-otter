package io.mustelidae.otter.lutrogale.api.domain.migration.openapi


import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.swagger.v3.oas.models.OpenAPI
import org.springframework.web.bind.annotation.RequestMethod
import java.util.concurrent.atomic.AtomicInteger

class PathToMenuTree {
    constructor(openApi: OpenAPI) {
        this.pathWithHttpMethods = PathCollector(openApi).collectPathAndMethods()
    }

    constructor(httpApiSpecs: List<HttpAPISpec>) {
        this.pathWithHttpMethods = httpApiSpecs
    }

    private var pathWithHttpMethods: List<HttpAPISpec>


    fun make(): MenuNavigation {
        val sortedPaths = pathWithHttpMethods.sortedBy { it.url }

        val rootMenuNavigation = MenuNavigation.root()
        val categoryMap = sortedPaths.groupBy {it.blocksQueue.removeFirst() }

        for (categoryEntry in categoryMap) {
            val atomicInt = AtomicInteger(1)

            val httpMethodSpec = categoryEntry.value.filter { it.blocksQueue.isEmpty() }

            for(spec in httpMethodSpec){
                for(method in spec.methods) {
                    val categoryMenuNavigation = MenuNavigation(
                        name = categoryEntry.key.replace("/",""),
                        type = Constant.NavigationType.CATEGORY,
                        uriBlock = categoryEntry.key,
                        methodType = method,
                        treeId = atomicInt.get().toString(),
                        parentTreeId = rootMenuNavigation.treeId
                    )
                    rootMenuNavigation.addBy(categoryMenuNavigation)
                }
            }
        }

        return rootMenuNavigation
    }
}
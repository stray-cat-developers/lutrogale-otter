package io.mustelidae.otter.lutrogale.api.domain.migration.openapi


import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.swagger.v3.oas.models.OpenAPI
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.RequestMethod

class PathToMenuTree {

    private var pathWithHttpMethods = emptyList<HttpAPISpec>()

    fun setOpenAPI(openApi: OpenAPI){
        this.pathWithHttpMethods = PathCollector(openApi).collectPathAndMethods()
    }

/*    fun make(): List<MenuNavigation> {
        val sortedPaths = pathWithHttpMethods.sortedBy { it.url }



    }

    fun parse(parentMenuNavigation: MenuNavigation, uriBlock: String, httpMethod: HttpMethod) {

        val exist = (parentMenuNavigation.menuNavigations.find { it.uriBlock == uriBlock && it.methodType == RequestMethod.valueOf(httpMethod.name()) } != null)

        if (!exist) {
            val menuNavigation = MenuNavigation(
                name = "",
                type = Constant.NavigationType.FUNCTION,
                uriBlock = uriBlock,
                methodType = RequestMethod.valueOf(httpMethod.name()),
                treeId = "",
                parentTreeId = ""
            )
            menuNavigation.setBy(parentMenuNavigation)
            parentMenuNavigation.addBy(menuNavigation)
        }


        val branch = path.split("/")
        if(branch.isNotEmpty()){

        }

        menuNavigation.menuNavigations.find { it }
    }*/
}
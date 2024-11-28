package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.RequestMethod

class PathToMenuTreeTest {

    @Test
    fun makeCode () {
        val specs = listOf(
            HttpAPISpec("/sample/hello/world/:id", "sample hello world", listOf(HttpMethod.POST, HttpMethod.GET)),
            HttpAPISpec("/sample/hello/:id", "sample hello", listOf(HttpMethod.POST, HttpMethod.GET)),
            HttpAPISpec("/sample/:id", "sample", listOf(HttpMethod.POST, HttpMethod.GET, HttpMethod.PUT)),
            HttpAPISpec("/never/:id", "never", listOf(HttpMethod.POST, HttpMethod.GET, HttpMethod.PUT)),
            HttpAPISpec("/never/:id/die", "never die", listOf(HttpMethod.POST, HttpMethod.GET, HttpMethod.PUT)),
        )

        val rootNavigation = MenuNavigation(
            "start",
            Constant.NavigationType.CATEGORY,
            "/",
            RequestMethod.GET,
            "1",
            "#",
        )

        val group = specs.groupBy { it.blocks.first() }

        group.forEach { t, u ->
            MenuNavigation()
        }






    }




    @Test
    fun makeCodeWithRecursive() {

        val httpAPISpec = HttpAPISpec("/sample/hello/world/:id", "테스트 api", listOf(HttpMethod.POST, HttpMethod.GET))

        val pathSegments = httpAPISpec.url.substring(1).split("/")

        val rootTree: MenuNavigation = MenuNavigation(pathSegments[0], Constant.NavigationType.FUNCTION, "", RequestMethod.GET, "#", "#")
        buildMenuTree(rootTree, pathSegments.drop(1), httpAPISpec.summary, httpAPISpec.methods)

        println("Root tree: $rootTree")
    }
}

fun buildMenuTree(parent: MenuNavigation, segments: List<String>, title: String, methods: List<HttpMethod>) {
    if(segments.isEmpty()) return

    val currentSegment = segments.first()
    val newTree = MenuNavigation(currentSegment, Constant.NavigationType.FUNCTION, title, RequestMethod.valueOf(methods.first().name()), "#", "#")

    parent.addBy(newTree)

    if (segments.size == 1) {
        methods.drop(1).forEach { method ->
            newTree.addBy(MenuNavigation(title, Constant.NavigationType.FUNCTION, currentSegment, RequestMethod.valueOf(methods.first().name()), "", ""))
        }
    } else {
        buildMenuTree(newTree, segments.drop(1), title, methods)
    }
}
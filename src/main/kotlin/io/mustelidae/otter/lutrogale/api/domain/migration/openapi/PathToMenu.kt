package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation

interface PathToMenu {
    var rootMenuNavigation: MenuNavigation

    fun makeTree()
    fun printMenuTree(): String
}

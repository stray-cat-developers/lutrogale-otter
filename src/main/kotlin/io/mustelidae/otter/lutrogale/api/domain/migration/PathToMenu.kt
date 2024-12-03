package io.mustelidae.otter.lutrogale.api.domain.migration

import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository

interface PathToMenu {
    var rootMenuNavigation: MenuNavigation

    fun makeTree(menuNavigationRepository: MenuNavigationRepository)
    fun printMenuTree(): String

    fun transformName(input: String): String {
        return input
            // {}와 : 제거
            .replace(Regex("[{}:/]"), " ")
            // 하이픈 처리
            .replace("-", " ")
            // 카멜케이스 처리
            .replace(Regex("([a-z])([A-Z])"), "$1 $2")
            // 대문자로 변환
            .uppercase()
    }
}

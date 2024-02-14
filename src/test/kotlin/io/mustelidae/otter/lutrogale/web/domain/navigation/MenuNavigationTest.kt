package io.mustelidae.otter.lutrogale.web.domain.navigation

import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.mustelidae.otter.lutrogale.web.domain.project.aFixture
import org.springframework.web.bind.annotation.RequestMethod

class MenuNavigationTest

fun MenuNavigation.Companion.aFixture(apiKey: String): MenuNavigation {
    return MenuNavigation(
        "Test",
        Constant.NavigationType.FUNCTION,
        "/",
        RequestMethod.GET,
        "1",
        "0",
    ).apply {
        setBy(Project.aFixture(apiKey))
    }
}

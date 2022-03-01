package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.commons.ApiRes.Companion.success
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationManager
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by HanJaehyun on 2016. 9. 27..
 */
@RestController
@RequestMapping(value = ["/project/{projectId}"])
class NavigationController(
    private val menuNavigationManager: MenuNavigationManager
) {

    @PutMapping("/navigation/{menuNavigationId}")
    fun modifyInfo(
        @PathVariable projectId: String,
        @PathVariable menuNavigationId: Long,
        @RequestBody modify: NavigationResources.Modify,
    ): ApiRes<*> {
        menuNavigationManager.modify(menuNavigationId, modify.name, modify.type, modify.methodType, modify.uriBlock)
        return success()
    }
}

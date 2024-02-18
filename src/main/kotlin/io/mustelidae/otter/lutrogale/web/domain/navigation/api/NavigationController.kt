package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReply
import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationInteraction
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "메뉴 네비게이션")
@LoginCheck
@RestController
@RequestMapping(value = ["/project/{projectId}"])
class NavigationController(
    private val menuNavigationInteraction: MenuNavigationInteraction,
) {

    @Operation(summary = "메뉴 네비게이션 수정")
    @PutMapping("/navigation/{menuNavigationId}")
    fun modifyInfo(
        @PathVariable projectId: String,
        @PathVariable menuNavigationId: Long,
        @RequestBody modify: NavigationResources.Modify,
    ): Reply<Unit> {
        menuNavigationInteraction.modify(menuNavigationId, modify.name, modify.type, modify.methodType, modify.uriBlock)
        return Unit.toReply()
    }
}

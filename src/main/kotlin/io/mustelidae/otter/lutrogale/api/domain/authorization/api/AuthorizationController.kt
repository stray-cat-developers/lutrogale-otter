package io.mustelidae.otter.lutrogale.api.domain.authorization.api

import io.mustelidae.otter.lutrogale.api.domain.authorization.AccessGrant
import io.mustelidae.otter.lutrogale.api.domain.authorization.ClientCertificationInteraction
import io.mustelidae.otter.lutrogale.api.permission.RoleHeader
import io.mustelidae.otter.lutrogale.common.Replies
import io.mustelidae.otter.lutrogale.common.toReplies
import io.mustelidae.otter.lutrogale.config.DataNotFindException
import io.mustelidae.otter.lutrogale.utils.toDecode
import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationInteraction
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "권한 체크", description = "이메일 사용자의 접근 권한 여부를 체크합니다.")
@LoginCheck(false)
@RestController
@RequestMapping("/v1/verification")
class AuthorizationController(
    private val clientCertificationInteraction: ClientCertificationInteraction,
    private val userFinder: UserFinder,
    private val projectFinder: ProjectFinder,
    private val menuNavigationInteraction: MenuNavigationInteraction,
) {

    @Operation(summary = "메뉴ID 기반 권한 체크")
    @PostMapping("authorization-check/id")
    fun idChecks(
        @RequestHeader(RoleHeader.XSystem.KEY) apiKey: String,
        @RequestBody @Valid
        request: AccessResources.Request.IdBase,
    ): Replies<AccessResources.Reply.AccessState> {
        val ids = request.ids
        val email = request.email
        if (!clientCertificationInteraction.isAuthorizedUserIfAddNotFoundUser(email)) {
            val accessStates = ids.map {
                AccessResources.Reply.AccessState.ofDenied(it, "최초 접근한 사용자이며 사용자의 권한 등록이 필요합니다.")
            }
            return accessStates.toReplies()
        }
        val accessGrant = AccessGrant.ofIdBase(email, apiKey, ids)
        val accessStates: List<AccessResources.Reply.AccessState> = clientCertificationInteraction.check(accessGrant)
        return accessStates.toReplies()
    }

    @Operation(summary = "호출 URL 기반 권한 체크")
    @PostMapping("authorization-check/uri")
    fun urlCheck(
        @RequestHeader(RoleHeader.XSystem.KEY) apiKey: String,
        @RequestBody @Valid
        request: AccessResources.Request.UriBase,
    ): Replies<AccessResources.Reply.AccessState> {
        val accessUris = request.uris.map { AccessResources.AccessUri.of(it.uri, it.methodType) }
        val email = request.email
        if (!clientCertificationInteraction.isAuthorizedUserIfAddNotFoundUser(email)) {
            val accessStates: MutableList<AccessResources.Reply.AccessState> = ArrayList()
            accessStates.addAll(
                accessUris.map { AccessResources.Reply.AccessState.ofDenied(it.uri, "최초 접근한 사용자이며 사용자의 권한 등록이 필요합니다.") },
            )
            return accessStates.toReplies()
        }
        val accessGrant = AccessGrant.ofUrlBase(email, apiKey, accessUris)
        val accessStates = clientCertificationInteraction.check(accessGrant)

        return accessStates.toReplies()
    }

    @Operation(summary = "사용자의 권한이 있는 모든 메뉴 조회")
    @GetMapping("accessible-urls")
    fun findAllAccessibleGrant(
        @RequestHeader(RoleHeader.XSystem.KEY) apiKey: String,
        @RequestParam email: String,
    ): Replies<AccessResources.AccessUri> {
        val userEmail = email.toDecode()
        val user = userFinder.findBy(userEmail) ?: throw DataNotFindException(userEmail, "사용자가 존재하지 않습니다.")
        val project = projectFinder.findByLiveProjectOfApiKey(apiKey)

        val groupMenuNavigations = user.authorityDefinitions
            .filter { it.project!!.id == project.id }
            .flatMap { it.menuNavigations }

        val personalMenuNavigations = user.menuNavigations
            .filter { it.project!!.id == project.id }

        val navigations = groupMenuNavigations.plus(personalMenuNavigations)

        val urls = navigations.map {
            AccessResources.AccessUri.of(menuNavigationInteraction.getFullUrl(it), it.methodType)
        }

        return urls.toReplies()
    }
}

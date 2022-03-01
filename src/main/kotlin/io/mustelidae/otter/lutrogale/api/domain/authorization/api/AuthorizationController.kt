package io.mustelidae.otter.lutrogale.api.domain.authorization.api

import io.mustelidae.otter.lutrogale.api.common.Replies
import io.mustelidae.otter.lutrogale.api.common.toReplies
import io.mustelidae.otter.lutrogale.api.domain.authorization.AccessUri
import io.mustelidae.otter.lutrogale.api.domain.authorization.ClientCertificationInteraction
import io.mustelidae.otter.lutrogale.web.commons.constant.OsoriConstant.NavigationType
import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.HumanErr
import io.mustelidae.otter.lutrogale.web.commons.exception.SystemErr
import io.mustelidae.otter.lutrogale.web.commons.utils.DecryptUtil.aes128
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationManager
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import io.mustelidae.otter.lutrogale.web.domain.user.User
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.URISyntaxException
import java.security.GeneralSecurityException
import javax.validation.Valid

/**
 * Created by seooseok on 2016. 9. 28..
 */
@RestController
@RequestMapping("/api")
class AuthorizationController(
    private val clientCertificationInteraction: ClientCertificationInteraction,
    private val userFinder: UserFinder,
    private val projectFinder: ProjectFinder,
    private val menuNavigationManager: MenuNavigationManager
) {

    /**
     * @api {post} /user/{email}/authorization-check/id [Id based check]
     * @apiVersion 0.1
     * @apiGroup authorization
     * @apiName 네비ID 권한 체크
     * @apiDescription 오소리 admin에 등록된 메뉴 네비게이션 ID를 가지고 권한을 체크 합니다.
     */
    @PostMapping("/user/{email}/authorization-check/id")
    fun idChecks(
        @PathVariable(value = "email") encryptedEmail: String?,
        @RequestParam(value = "id") idGroup: List<Long>,
        @RequestParam apiKey: String
    ): Replies<AccessResource> {
        val email: String = decryptEmail(apiKey, encryptedEmail)
        if (!clientCertificationInteraction.isAuthorizedUser(email)) {
            val accessResources: MutableList<AccessResource> = ArrayList()
            accessResources.addAll(
                idGroup.map {
                    AccessResource.ofDenied(
                        it,
                        "최초 접근한 유저이며 유저의 권한 등록이 필요합니다."
                    )
                }.toList()

            )
            return accessResources.toReplies()
        }
        val checkResource: AuthenticationCheckResource = AuthenticationCheckResource.ofIdBase(email, apiKey, idGroup)
        val accessResources: List<AccessResource> = clientCertificationInteraction.check(checkResource)
        return accessResources.toReplies()
    }

    @PostMapping("/user/{email}/authorization-check/uri")
    fun urlCheck(
        @PathVariable(value = "email") encryptedEmail: String,
        @RequestBody request: @Valid UriBaseAuthorityCheckRequest
    ): Replies<AccessResource> {
        val email: String
        val accessUris: MutableList<AccessUri> = ArrayList()
        try {
            email = decryptEmail(request.apiKey, encryptedEmail)
            var uri: URI
            for (uriRequest in request.accessUriRequests) {
                uri = URI(uriRequest.getDecryptedUri(request.apiKey))
                accessUris.add(AccessUri.of(uri.path, RequestMethod.valueOf(uriRequest.methodType)))
            }
        } catch (e: GeneralSecurityException) {
            throw ApplicationException(SystemErr.ERROR_ENCRYPT)
        } catch (e: URISyntaxException) {
            throw ApplicationException(HumanErr.INVALID_URL)
        }

        if (!clientCertificationInteraction.isAuthorizedUser(email)) {
            val accessResources: MutableList<AccessResource> = ArrayList()
            accessResources.addAll(
                accessUris.map { AccessResource.ofDenied(it.uri, "최초 접근한 유저이며 유저의 권한 등록이 필요합니다.") }
            )
            return accessResources.toReplies()
        }
        val checkResource: AuthenticationCheckResource =
            AuthenticationCheckResource.ofUrlBase(email, request.apiKey, accessUris)
        val accessResources: List<AccessResource> = clientCertificationInteraction.check(checkResource)

        return accessResources.toReplies()
    }

    @GetMapping("/user/{email}/project/{projectId}/urls")
    fun accessibleList(
        @PathVariable(value = "email") encryptedEmail: String,
        @PathVariable(value = "projectId") projectId: Long,
        @RequestParam apiKey: String,
        @RequestParam(required = false) type: NavigationType?
    ): Replies<AccessUri> {
        val email: String = decryptEmail(apiKey, encryptedEmail)

        val user: User = userFinder.findBy(email)

        val project = projectFinder.findByLive(projectId)

        if (type != null && !listOf(*NavigationType.values()).contains(type))
            throw ApplicationException(HumanErr.INVALID_ARGS)

        var groupMenuNavigations = user.authorityDefinitions
            .filter { it.project!!.id == project.id }
            .flatMap { it.menuNavigations }

        var personalMenuNavigations = user.menuNavigations
            .filter { it.project!!.id == project.id }

        if (type != null) {
            groupMenuNavigations = groupMenuNavigations.filter { it.type == type }
            personalMenuNavigations = personalMenuNavigations.filter { it.type == type }
        }

        val navigations = groupMenuNavigations.plus(personalMenuNavigations)

        val urls = navigations.map {
            AccessUri.of(menuNavigationManager.getFullUrl(it), it.methodType)
        }

        return urls.toReplies()
    }

    private fun decryptEmail(apiKey: String, encryptedEmail: String?): String {
        val email: String = try {
            aes128(apiKey.substring(0, 16), encryptedEmail)
        } catch (e: GeneralSecurityException) {
            throw ApplicationException(SystemErr.ERROR_ENCRYPT)
        }
        return email
    }
}

package io.mustelidae.otter.lutrogale.web.domain.grant

import io.mustelidae.smoothcoatedotter.web.commons.constant.OsoriConstant.AuthenticationCheckType
import io.mustelidae.smoothcoatedotter.web.commons.exception.ApplicationException
import io.mustelidae.smoothcoatedotter.web.commons.exception.HumanErr
import io.mustelidae.smoothcoatedotter.api.domain.authorization.AccessCheckerHandler
import io.mustelidae.smoothcoatedotter.api.domain.authorization.api.AccessResource
import io.mustelidae.smoothcoatedotter.api.domain.authorization.api.AuthenticationCheckResource
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigation
import io.mustelidae.smoothcoatedotter.web.domain.project.Project
import io.mustelidae.smoothcoatedotter.web.domain.project.ProjectManager
import io.mustelidae.smoothcoatedotter.web.domain.user.User
import io.mustelidae.smoothcoatedotter.web.domain.user.UserManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

/**
 * Created by seooseok on 2016. 10. 14..
 * 권한 인증 서비스
 */
@Service
@Transactional
class ClientAuthentication(
    private val userManager: UserManager,
    private val projectManager: ProjectManager,
    private val accessCheckerHandler: AccessCheckerHandler

) {

    fun isAuthorizedUser(email: String): Boolean {
        val user = userManager.findBy(email)
        return user.status === User.Status.allow
    }

    fun check(checkResource: AuthenticationCheckResource): List<AccessResource> {
        val project = projectManager.findByLiveProjectOfApiKey(checkResource.apiKey)

        val user: User = userManager.findBy(checkResource.email)
        val sourceNavigationGroup = getNavigationsOfUser(user, project)
        for (menuNavigation in sourceNavigationGroup) {
            if (menuNavigation.project != project) throw ApplicationException(HumanErr.INVALID_ACCESS)
        }
        val checkType: AuthenticationCheckType = checkResource.authenticationCheckType
        val accessChecker = accessCheckerHandler.handle(checkType)
        return accessChecker.validate(sourceNavigationGroup, checkResource)
    }

    private fun getNavigationsOfUser(user: User, project: Project): List<MenuNavigation> {
        val menuNavigations: MutableList<MenuNavigation> = ArrayList()
        val authorityDefinitions = user.authorityDefinitions
        if (authorityDefinitions.isEmpty()) throw ApplicationException(
            HumanErr.INVALID_ACCESS,
            "해당 사용자는 권한 설정이 되어있지 않습니다."
        )
        for (authorityDefinition in authorityDefinitions) {
            menuNavigations.addAll(authorityDefinition.menuNavigations)
        }
        menuNavigations.addAll(user.menuNavigations)
        return project.menuNavigations
            .stream()
            .filter { o: MenuNavigation -> menuNavigations.contains(o) }
            .collect(Collectors.toList())
    }
}

package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources
import io.mustelidae.otter.lutrogale.web.commons.constant.OsoriConstant.AuthenticationCheckType
import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.HumanErr
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import io.mustelidae.otter.lutrogale.web.domain.user.User
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import io.mustelidae.otter.lutrogale.web.domain.user.UserInteraction
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by seooseok on 2016. 10. 14..
 * 권한 인증 서비스
 */
@Service
@Transactional
class ClientCertificationInteraction(
    private val userFinder: UserFinder,
    private val userInteraction: UserInteraction,
    private val projectFinder: ProjectFinder,
    private val accessCheckerHandler: AccessCheckerHandler

) {

    fun isAuthorizedUserIfAddNotFoundUser(email: String): Boolean {
        val user = userFinder.findBy(email)
        return if (user == null) {
            userInteraction.createBy(email, "Known User", User.Status.wait)
            false
        } else {
            user.status === User.Status.allow
        }
    }

    fun check(checkResource: AccessGrant): List<AccessResources.Reply.AccessState> {
        val project = projectFinder.findByLiveProjectOfApiKey(checkResource.apiKey)

        val user: User = userFinder.findBy(checkResource.email)!!

        val menuNavigations = getNavigationsOfUser(user, project)
        for (menuNavigation in menuNavigations) {
            if (menuNavigation.project != project)
                throw ApplicationException(HumanErr.INVALID_ACCESS)
        }
        val checkType: AuthenticationCheckType = checkResource.authenticationCheckType
        val accessChecker = accessCheckerHandler.handle(checkType)
        return accessChecker.validate(menuNavigations, checkResource)
    }

    private fun getNavigationsOfUser(user: User, project: Project): List<MenuNavigation> {
        val menuNavigations: MutableList<MenuNavigation> = ArrayList()
        val authorityDefinitions = user.authorityDefinitions
        if (authorityDefinitions.isEmpty())
            throw ApplicationException(HumanErr.INVALID_ACCESS, "해당 사용자는 권한 설정이 되어있지 않습니다.")

        menuNavigations.addAll(authorityDefinitions.flatMap { it.menuNavigations })
        menuNavigations.addAll(user.menuNavigations)

        return menuNavigations.filter { it.project == project }
    }
}

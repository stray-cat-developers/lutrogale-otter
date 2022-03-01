package io.mustelidae.otter.lutrogale.web.domain.user

import io.mustelidae.smoothcoatedotter.web.commons.exception.ApplicationException
import io.mustelidae.smoothcoatedotter.web.commons.exception.HumanErr
import io.mustelidae.smoothcoatedotter.web.domain.grant.AuthorityDefinition
import io.mustelidae.smoothcoatedotter.web.domain.grant.AuthorityDefinitionManager
import io.mustelidae.smoothcoatedotter.web.domain.grant.api.AuthorityGrantResource
import io.mustelidae.smoothcoatedotter.web.domain.grant.api.PersonalGrantResource
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigation
import io.mustelidae.smoothcoatedotter.web.domain.navigation.MenuNavigationManager
import io.mustelidae.smoothcoatedotter.web.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Transactional
@Service
class UserGrant(
    private val userManager: UserManager,
    private val userRepository: UserRepository,
    private val authorityDefinitionManager: AuthorityDefinitionManager,
    private val menuNavigationManager: MenuNavigationManager
) {

    fun getUserAuthorityGrants(id: Long, projectId: Long): List<AuthorityGrantResource> {
        val user = userManager.findBy(id)

        return user.userAuthorityGrants
            .filter { it.authorityDefinition!!.project!!.id == projectId }
            .map {
                AuthorityGrantResource.of(it.authorityDefinition!!, it.createdAt!!)
            }
    }

    fun getUserPersonalGrants(id: Long, projectId: Long): List<PersonalGrantResource> {
        val user = userManager.findBy(id)

        return user.userPersonalGrants.filter { it.menuNavigation!!.project!!.id == projectId }
            .map {
                PersonalGrantResource.of(
                    it.menuNavigation!!,
                    it.createdAt!!,
                    menuNavigationManager.getFullUrl(it.menuNavigation!!)
                )
            }
    }

    fun addByAuthorityGrant(userId: Long, projectId: Long, authorityDefinitionIds: List<Long>) {
        val user = userManager.findByStatusAllow(userId)
        val authorityDefinitions: List<AuthorityDefinition> =
            authorityDefinitionManager.findByLive(authorityDefinitionIds)
        for (authorityDefinition in authorityDefinitions) {
            if (authorityDefinition.project!!.id != projectId) throw ApplicationException(HumanErr.INVALID_INCLUDE)
            user.addBy(authorityDefinition)
        }
        userRepository.save(user)
    }

    fun removeByAuthorityGrant(userId: Long, projectId: Long, authorityDefinitionIdGroup: List<Long>) {
        val user = userManager.findByStatusAllow(userId)
        val authorityDefinitions: List<AuthorityDefinition> =
            authorityDefinitionManager.findByLive(authorityDefinitionIdGroup)
        for (authorityDefinition in authorityDefinitions) {
            if (authorityDefinition.project!!.id != projectId) throw ApplicationException(HumanErr.INVALID_INCLUDE)
            user.removeBy(authorityDefinition)
        }
        userRepository.save(user)
    }

    fun addByPersonalGrant(userId: Long, projectId: Long, menuNavigationIds: List<Long>) {
        val user = userManager.findByStatusAllow(userId)
        val menuNavigations: List<MenuNavigation> = menuNavigationManager.findByLive(menuNavigationIds)
        for (menuNavigation in menuNavigations) {
            if (menuNavigation.project!!.id != projectId) throw ApplicationException(HumanErr.INVALID_INCLUDE)
            user.addBy(menuNavigation)
        }
        userRepository.save(user)
    }

    fun removeByPersonalGrant(userId: Long, projectId: Long, menuNavigationIds: List<Long>) {
        val user = userManager.findByStatusAllow(userId)
        val menuNavigations: List<MenuNavigation> = menuNavigationManager.findByLive(menuNavigationIds)
        for (menuNavigation in menuNavigations) {
            if (menuNavigation.project!!.id != projectId) throw ApplicationException(HumanErr.INVALID_INCLUDE)
            user.removeBy(menuNavigation)
        }
        userRepository.save(user)
    }
}

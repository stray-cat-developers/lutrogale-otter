package io.mustelidae.otter.lutrogale.web.domain.grant

import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.HumanErr
import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityDefinitionFinder
import io.mustelidae.otter.lutrogale.web.domain.grant.api.AuthorityGrantResource
import io.mustelidae.otter.lutrogale.web.domain.grant.api.PersonalGrantResource
import io.mustelidae.otter.lutrogale.web.domain.grant.repository.UserAuthorityGrantRepository
import io.mustelidae.otter.lutrogale.web.domain.grant.repository.UserPersonalGrantRepository
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationManager
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Transactional
@Service
class UserGrantInteraction(
    private val userFinder: UserFinder,
    private val authorityDefinitionFinder: AuthorityDefinitionFinder,
    private val menuNavigationManager: MenuNavigationManager,
    private val userAuthorityGrantRepository: UserAuthorityGrantRepository,
    private val userPersonalGrantRepository: UserPersonalGrantRepository,
    private val userAuthorityGrantFinder: UserAuthorityGrantFinder,
    private val userPersonalGrantFinder: UserPersonalGrantFinder
) {

    fun getUserAuthorityGrants(id: Long, projectId: Long): List<AuthorityGrantResource> {
        val user = userFinder.findBy(id)

        return user.userAuthorityGrants
            .filter { it.authorityDefinition!!.project!!.id == projectId }
            .map {
                AuthorityGrantResource.of(it.authorityDefinition!!, it.createdAt!!)
            }
    }

    fun getUserPersonalGrants(id: Long, projectId: Long): List<PersonalGrantResource> {
        val user = userFinder.findBy(id)

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
        val user = userFinder.findByStatusAllow(userId)
        val authorityDefinitions = authorityDefinitionFinder.findByLive(authorityDefinitionIds)

        for (authorityDefinition in authorityDefinitions) {
            if (authorityDefinition.project!!.id!! != projectId)
                throw ApplicationException(HumanErr.INVALID_INCLUDE)

            if (user.authorityDefinitions.contains(authorityDefinition))
                throw IllegalStateException("이미 해당 권한 그룹은 허용되어 있습니다.")
        }

        val mappingGrants = authorityDefinitions.map {
            UserAuthorityGrant().apply {
                setBy(user)
                setBy(it)
            }
        }

        userAuthorityGrantRepository.saveAll(mappingGrants)
    }

    fun removeByAuthorityGrant(userId: Long, projectId: Long, authorityDefinitionIdGroup: List<Long>) {
        userFinder.findByStatusAllow(userId)
        val authorityDefinitions = authorityDefinitionFinder.findByLive(authorityDefinitionIdGroup)

        for (authorityDefinition in authorityDefinitions) {
            if (authorityDefinition.project!!.id != projectId)
                throw ApplicationException(HumanErr.INVALID_INCLUDE)
        }

        val mappingGrants = userAuthorityGrantFinder.findByUserAndDefinition(userId, authorityDefinitionIdGroup)

        mappingGrants.map { it.expire() }
        userAuthorityGrantRepository.saveAll(mappingGrants)
    }

    fun addByPersonalGrant(userId: Long, projectId: Long, menuNavigationIds: List<Long>) {
        val user = userFinder.findByStatusAllow(userId)
        val menuNavigations: List<MenuNavigation> = menuNavigationManager.findByLive(menuNavigationIds)
        for (menuNavigation in menuNavigations) {
            if (menuNavigation.project!!.id != projectId)
                throw ApplicationException(HumanErr.INVALID_INCLUDE)

            if (user.menuNavigations.contains(menuNavigation))
                throw IllegalStateException("이미 해당 권한 메뉴는 허용되어 있습니다.")
        }

        val mappingGrants = menuNavigations.map {
            UserPersonalGrant().apply {
                setBy(user)
                setBy(it)
            }
        }
        userPersonalGrantRepository.saveAll(mappingGrants)
    }

    fun removeByPersonalGrant(userId: Long, projectId: Long, menuNavigationIds: List<Long>) {
        userFinder.findByStatusAllow(userId)
        val menuNavigations: List<MenuNavigation> = menuNavigationManager.findByLive(menuNavigationIds)

        for (menuNavigation in menuNavigations) {
            if (menuNavigation.project!!.id != projectId)
                throw ApplicationException(HumanErr.INVALID_INCLUDE)
        }
        val mappingGrants = userPersonalGrantFinder.findByUserAndMenu(userId, menuNavigationIds)

        mappingGrants.map { it.expire() }

        userPersonalGrantRepository.saveAll(mappingGrants)
    }
}

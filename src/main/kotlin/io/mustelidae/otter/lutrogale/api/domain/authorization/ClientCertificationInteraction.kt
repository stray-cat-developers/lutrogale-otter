package io.mustelidae.otter.lutrogale.api.domain.authorization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources
import io.mustelidae.otter.lutrogale.common.Constant.AuthenticationCheckType
import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.DevelopMistakeException
import io.mustelidae.otter.lutrogale.config.HumanException
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import io.mustelidae.otter.lutrogale.web.domain.user.User
import io.mustelidae.otter.lutrogale.web.domain.user.UserFinder
import io.mustelidae.otter.lutrogale.web.domain.user.UserInteraction
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

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
    private val accessCheckerHandler: AccessCheckerHandler,
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun isAuthorizedUserIfAddNotFoundUser(email: String): Boolean {
        val user = userFinder.findBy(email)
        return if (user == null) {
            userInteraction.createBy(email, "Known User", User.Status.WAIT)
            false
        } else {
            user.status === User.Status.ALLOW
        }
    }

    fun check(checkResource: AccessGrant): List<AccessResources.Reply.AccessState> {
        return when (checkResource.authenticationCheckType) {
            AuthenticationCheckType.ID -> {
                val ids = checkResource.menuNavigationIdGroup
                    ?: throw DevelopMistakeException("ID 체크에는 menuNavigationIdGroup이 필요합니다")
                val key = IdBaseAuthorizedKey(checkResource.apiKey, checkResource.email, ids).getKey()
                checkWithCache(key, IdBaseAuthorizedKey.TTL, checkResource)
            }
            AuthenticationCheckType.URI -> {
                val uris = checkResource.accessUriGroup
                    ?: throw DevelopMistakeException("URI 체크에는 accessUriGroup이 필요합니다")
                val key = UriBaseAuthorizedKey(checkResource.apiKey, checkResource.email, uris).getKey()
                checkWithCache(key, UriBaseAuthorizedKey.TTL, checkResource)
            }
        }
    }

    private fun checkWithCache(cacheKey: String, ttl: Duration, checkResource: AccessGrant): List<AccessResources.Reply.AccessState> {
        try {
            val cached = redisTemplate.opsForValue().get(cacheKey)
            if (cached != null) {
                return objectMapper.readValue(cached)
            }
        } catch (e: Exception) {
            log.warn("Redis 읽기 실패, DB에서 직접 조회합니다. key={}", cacheKey, e)
        }
        val result = doCheck(checkResource)
        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(result), ttl)
        } catch (e: Exception) {
            log.warn("Redis 쓰기 실패. key={}", cacheKey, e)
        }
        return result
    }

    private fun doCheck(checkResource: AccessGrant): List<AccessResources.Reply.AccessState> {
        val project = projectFinder.findByLiveProjectOfApiKey(checkResource.apiKey)

        val user: User = userFinder.findBy(checkResource.email)!!

        val menuNavigations = getNavigationsOfUser(user, project)
        for (menuNavigation in menuNavigations) {
            if (menuNavigation.project != project) {
                throw HumanException(DefaultError(ErrorCode.HA00, "해당 사용자는 권한 설정이 되어있지 않습니다."))
            }
        }
        val checkType: AuthenticationCheckType = checkResource.authenticationCheckType
        val accessChecker = accessCheckerHandler.handle(checkType)
        return accessChecker.validate(menuNavigations, checkResource)
    }

    private fun getNavigationsOfUser(user: User, project: Project): List<MenuNavigation> {
        val menuNavigations: MutableList<MenuNavigation> = ArrayList()
        val authorityDefinitions = user.authorityDefinitions
        if (authorityDefinitions.isEmpty()) {
            throw HumanException(DefaultError(ErrorCode.HA00, "해당 사용자는 권한 설정이 되어있지 않습니다."))
        }

        menuNavigations.addAll(authorityDefinitions.flatMap { it.menuNavigations })
        menuNavigations.addAll(user.menuNavigations)

        return menuNavigations.filter { it.project == project }
    }
}

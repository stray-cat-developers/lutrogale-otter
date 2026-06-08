package io.mustelidae.otter.lutrogale.api.domain.authorization.api

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mustelidae.otter.lutrogale.api.config.EmbeddedDbInitializer
import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import io.mustelidae.otter.lutrogale.api.domain.authorization.IdBaseAuthorizedKey
import io.mustelidae.otter.lutrogale.api.domain.authorization.UriBaseAuthorizedKey
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources
import io.mustelidae.otter.lutrogale.web.domain.project.repository.ProjectRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.RequestMethod

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AuthorizationControllerTest : FlowTestSupport() {

    @Autowired private lateinit var projectRepository: ProjectRepository

    @Autowired private lateinit var redisTemplate: StringRedisTemplate

    @BeforeEach
    fun clearCache() {
        redisTemplate.connectionFactory?.connection?.flushAll()
    }

    @Test
    fun idChecks() {
        val userEmail = EmbeddedDbInitializer.USER_EMAIL
        val authFlow = AuthorizationControllerFlow(projectRepository, mockMvc)
        val accessStates = authFlow.idCheck(userEmail, listOf(1, 2, 3))

        accessStates.size shouldBe 3
        accessStates.first().asClue {
            it.hasPermission shouldBe true
            it.checkWay
        }
    }

    @Test
    fun idChecksNotUser() {
        val userEmail = "abc@test.com"
        val authFlow = AuthorizationControllerFlow(projectRepository, mockMvc)
        val firstAccess = authFlow.idCheck(userEmail, listOf(1))

        firstAccess.first().hasPermission shouldBe false

        val secondAccess = authFlow.idCheck(userEmail, listOf(1))
        secondAccess.first().hasPermission shouldBe false
    }

    @Test
    fun idChecksNoPermission() {
        val userEmail = EmbeddedDbInitializer.USER_EMAIL
        val authFlow = AuthorizationControllerFlow(projectRepository, mockMvc)
        val replies = authFlow.idCheck(userEmail, listOf(4))

        replies.first().hasPermission shouldBe false
    }

    @Test
    fun idChecksCachesResult() {
        val userEmail = EmbeddedDbInitializer.USER_EMAIL
        val authFlow = AuthorizationControllerFlow(projectRepository, mockMvc)
        val ids = listOf(1L, 2L, 3L)
        val apiKey = projectRepository.findAll().first().apiKey

        val firstResult = authFlow.idCheck(userEmail, ids)

        val cacheKey = IdBaseAuthorizedKey(apiKey, userEmail, ids).getKey()
        redisTemplate.opsForValue().get(cacheKey) shouldNotBe null

        val secondResult = authFlow.idCheck(userEmail, ids)

        secondResult.size shouldBe firstResult.size
        secondResult.forEachIndexed { index, state ->
            state.hasPermission shouldBe firstResult[index].hasPermission
            state.target shouldBe firstResult[index].target
        }
    }

    @Test
    fun uriCheckCachesResult() {
        val userEmail = EmbeddedDbInitializer.USER_EMAIL
        val authFlow = AuthorizationControllerFlow(projectRepository, mockMvc)
        val url = "/applications/1234/reviews/1234"
        val method = RequestMethod.GET
        val apiKey = projectRepository.findAll().first().apiKey

        val firstResult = authFlow.uriCheck(userEmail, url, method)

        val cacheKey = UriBaseAuthorizedKey(apiKey, userEmail, listOf(AccessResources.AccessUri(url, method))).getKey()
        redisTemplate.opsForValue().get(cacheKey) shouldNotBe null

        val secondResult = authFlow.uriCheck(userEmail, url, method)

        secondResult.size shouldBe firstResult.size
        secondResult.first().hasPermission shouldBe firstResult.first().hasPermission
        secondResult.first().target shouldBe firstResult.first().target
    }

    @Test
    fun graphqlCheckCachesResult() {
        val userEmail = EmbeddedDbInitializer.USER_EMAIL
        val authFlow = AuthorizationControllerFlow(projectRepository, mockMvc)
        val operation = "getReviews"
        val method = RequestMethod.GET
        val apiKey = projectRepository.findAll().first().apiKey

        val firstResult = authFlow.graphqlCheck(userEmail, operation, method)

        val convertedUri = "/$operation"
        val cacheKey = UriBaseAuthorizedKey(apiKey, userEmail, listOf(AccessResources.AccessUri(convertedUri, method))).getKey()
        redisTemplate.opsForValue().get(cacheKey) shouldNotBe null

        val secondResult = authFlow.graphqlCheck(userEmail, operation, method)

        secondResult.size shouldBe firstResult.size
        secondResult.first().hasPermission shouldBe firstResult.first().hasPermission
        secondResult.first().target shouldBe firstResult.first().target
    }

    @Test
    fun idChecksWithCorruptedCacheFallsBackToDb() {
        val userEmail = EmbeddedDbInitializer.USER_EMAIL
        val authFlow = AuthorizationControllerFlow(projectRepository, mockMvc)
        val ids = listOf(1L, 2L, 3L)
        val apiKey = projectRepository.findAll().first().apiKey

        // 역직렬화 불가능한 값을 캐시에 직접 저장
        val cacheKey = IdBaseAuthorizedKey(apiKey, userEmail, ids).getKey()
        redisTemplate.opsForValue().set(cacheKey, "INVALID_JSON")

        // 캐시 오염 시 DB에서 정상 조회하여 응답해야 함
        val result = authFlow.idCheck(userEmail, ids)

        result.size shouldBe 3
        result.first().hasPermission shouldBe true
    }

    @Test
    fun urlCheck() {
        // Given
        val url = "/applications/1234/reviews/1234"
        val userEmail = EmbeddedDbInitializer.USER_EMAIL
        val authFlow = AuthorizationControllerFlow(projectRepository, mockMvc)

        // When
        val access = authFlow.uriCheck(userEmail, url, RequestMethod.GET).first()

        access.hasPermission shouldBe true
        access.checkWay shouldBe AccessResources.CheckWay.URI
    }

    @Test
    fun urlCheck2() {
        // Given
        val url = "/applications/1234/reviews/abcd/adf"
        val userEmail = EmbeddedDbInitializer.USER_EMAIL
        val authFlow = AuthorizationControllerFlow(projectRepository, mockMvc)

        // When
        val access = authFlow.uriCheck(userEmail, url, RequestMethod.GET).first()

        access.hasPermission shouldBe false
        access.checkWay shouldBe AccessResources.CheckWay.URI
    }

    @Test
    fun accessibleList() {
        // Given
        val userEmail = EmbeddedDbInitializer.USER_EMAIL
        val authFlow = AuthorizationControllerFlow(projectRepository, mockMvc)

        // When
        val grants = authFlow.findAllAccessibleGrant(userEmail)
        // Then
        grants.size shouldBe 4

        grants.asClue {
            val accessUri = it.find { accessUri -> accessUri.methodType == RequestMethod.POST }!!
            accessUri.uri shouldBe "/applications/{name}/reviews/{reviewId}"
        }
    }
}

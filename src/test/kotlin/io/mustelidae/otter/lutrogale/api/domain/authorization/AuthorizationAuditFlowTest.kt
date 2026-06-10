package io.mustelidae.otter.lutrogale.api.domain.authorization

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotContain
import io.mustelidae.otter.lutrogale.api.config.EmbeddedDbInitializer
import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AuthorizationControllerFlow
import io.mustelidae.otter.lutrogale.api.permission.RoleHeader
import io.mustelidae.otter.lutrogale.web.domain.project.repository.ProjectRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import org.springframework.web.bind.annotation.RequestMethod

internal class AuthorizationAuditFlowTest : FlowTestSupport() {
    @Autowired private lateinit var projectRepository: ProjectRepository

    @Autowired private lateinit var objectMapper: ObjectMapper

    private val auditLogger = LoggerFactory.getLogger("audit.authorization") as Logger
    private val listAppender = ListAppender<ILoggingEvent>()

    @BeforeEach
    fun attachAppender() {
        listAppender.list.clear()
        listAppender.start()
        auditLogger.addAppender(listAppender)
    }

    @AfterEach
    fun detachAppender() {
        auditLogger.detachAppender(listAppender)
    }

    @Test
    fun `URI 기반 권한 체크 시 감사 로그가 기록된다`() {
        val flow = AuthorizationControllerFlow(projectRepository, mockMvc)

        flow.uriCheck(EmbeddedDbInitializer.USER_EMAIL, "/applications/1234/reviews/1234", RequestMethod.GET)

        listAppender.list.size shouldBeGreaterThan 0
        val entry = objectMapper.readValue<Map<String, Any>>(listAppender.list.last().message)

        entry["event"] shouldBe "authorization_check"
        entry["email"] shouldBe EmbeddedDbInitializer.USER_EMAIL
        entry["checkType"] shouldBe "URI"
        (entry["allowed"] as Boolean).shouldBeTrue()
        entry["deniedCount"] shouldBe 0
        entry["totalCount"] shouldBe 1
        entry["latencyMs"] shouldNotBe null
    }

    @Test
    fun `감사 로그에 apiKey는 마스킹되어 기록된다`() {
        val flow = AuthorizationControllerFlow(projectRepository, mockMvc)
        val rawApiKey = projectRepository.findAll().first().apiKey

        flow.uriCheck(EmbeddedDbInitializer.USER_EMAIL, "/applications/1234/reviews/1234", RequestMethod.GET)

        val entry = objectMapper.readValue<Map<String, Any>>(listAppender.list.last().message)
        val maskedApiKey = entry["apiKey"] as String
        maskedApiKey shouldEndWith "****"
        maskedApiKey shouldNotContain rawApiKey
    }

    @Test
    fun `ID 기반 권한 체크 시 감사 로그 checkType이 ID다`() {
        val flow = AuthorizationControllerFlow(projectRepository, mockMvc)

        flow.idCheck(EmbeddedDbInitializer.USER_EMAIL, listOf(1, 2, 3))

        val entry = objectMapper.readValue<Map<String, Any>>(listAppender.list.last().message)
        entry["checkType"] shouldBe "ID"
        entry["email"] shouldBe EmbeddedDbInitializer.USER_EMAIL
        entry["totalCount"] shouldBe 3
    }

    @Test
    fun `GraphQL 기반 권한 체크 시 감사 로그 checkType이 GRAPHQL이다`() {
        val flow = AuthorizationControllerFlow(projectRepository, mockMvc)

        flow.graphqlCheck(EmbeddedDbInitializer.USER_EMAIL, "getReviews", RequestMethod.GET)

        val entry = objectMapper.readValue<Map<String, Any>>(listAppender.list.last().message)
        entry["checkType"] shouldBe "GRAPHQL"
        entry["email"] shouldBe EmbeddedDbInitializer.USER_EMAIL
    }

    @Test
    fun `신규 사용자 요청 시 감사 로그에 denied로 기록된다`() {
        val flow = AuthorizationControllerFlow(projectRepository, mockMvc)
        val newUserEmail = "newuser-audit-test@example.com"

        flow.uriCheck(newUserEmail, "/applications/1234/reviews/1234", RequestMethod.GET)

        val entry = objectMapper.readValue<Map<String, Any>>(listAppender.list.last().message)
        (entry["allowed"] as Boolean).shouldBeFalse()
        entry["deniedCount"] shouldBe 1
        entry["totalCount"] shouldBe 1
    }

    @Test
    fun `잘못된 apiKey로 요청 시 예외가 발생해도 감사 로그에 allowed=false로 기록된다`() {
        val request =
            AccessResources.Request.UriBase(
                EmbeddedDbInitializer.USER_EMAIL,
                listOf(AccessResources.AccessUri("/applications/1234/reviews/1234", RequestMethod.GET)),
            )

        mockMvc
            .post("/v1/verification/authorization-check/uri") {
                contentType = MediaType.APPLICATION_JSON
                header(RoleHeader.XSystem.KEY, "invalid-api-key-that-does-not-exist")
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { is4xxClientError() }
            }

        listAppender.list.size shouldBeGreaterThan 0
        val entry = objectMapper.readValue<Map<String, Any>>(listAppender.list.last().message)
        (entry["allowed"] as Boolean).shouldBeFalse()
        entry["checkType"] shouldBe "URI"
        entry["totalCount"] shouldBe 0
    }
}

package io.mustelidae.otter.lutrogale.web.domain.user.api

import io.kotest.matchers.shouldBe
import io.mustelidae.otter.lutrogale.api.config.EmbeddedDbInitializer
import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminInteraction
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminRole
import io.mustelidae.otter.lutrogale.web.domain.authority.repository.AuthorityDefinitionRepository
import io.mustelidae.otter.lutrogale.web.domain.grant.UserGrantInteraction
import io.mustelidae.otter.lutrogale.web.domain.project.repository.ProjectRepository
import io.mustelidae.otter.lutrogale.web.domain.user.User
import io.mustelidae.otter.lutrogale.web.domain.user.repository.UserRepository
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class UserBulkRegisterControllerTest : FlowTestSupport() {
    @Autowired
    private lateinit var adminInteraction: AdminInteraction

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var authorityDefinitionRepository: AuthorityDefinitionRepository

    @Autowired
    private lateinit var userGrantInteraction: UserGrantInteraction

    private lateinit var flow: UserBulkRegisterControllerFlow
    private lateinit var superSession: Cookie
    private var projectId: Long = 0
    private var authorityDefinitionId: Long = 0

    @BeforeEach
    fun setUp() {
        flow = UserBulkRegisterControllerFlow(mockMvc)
        superSession = flow.login("admin@osori.com", "admin")
        projectId = projectRepository.findAll().first().id!!
        authorityDefinitionId = authorityDefinitionRepository.findAll().first()!!.id!!
    }

    @Test
    fun `SUPER 어드민이 신규 이메일 목록을 등록하면 모두 SUCCESS를 반환한다`() {
        val request =
            UserResources.Request.BatchRegister(
                emails = listOf("bulk1@test.com", "bulk2@test.com"),
                projectId = null,
                authorityDefinitionId = null,
                initialStatus = User.Status.ALLOW,
            )

        val results = flow.bulkRegister(superSession, request)

        results.size shouldBe 2
        results.all { it.outcome == UserResources.Reply.BatchRegister.Outcome.SUCCESS } shouldBe true
        results.all { it.userId != null } shouldBe true
    }

    @Test
    fun `이미 등록된 이메일은 SKIPPED를 반환하고 신규 이메일은 SUCCESS를 반환한다`() {
        val request =
            UserResources.Request.BatchRegister(
                emails = listOf(EmbeddedDbInitializer.USER_EMAIL, "new-user@test.com"),
                projectId = null,
                authorityDefinitionId = null,
                initialStatus = User.Status.ALLOW,
            )

        val results = flow.bulkRegister(superSession, request)

        results.size shouldBe 2
        results.first { it.email == EmbeddedDbInitializer.USER_EMAIL }.outcome shouldBe UserResources.Reply.BatchRegister.Outcome.SKIPPED
        results.first { it.email == "new-user@test.com" }.outcome shouldBe UserResources.Reply.BatchRegister.Outcome.SUCCESS
    }

    @Test
    fun `전부 중복 이메일이면 전부 SKIPPED를 반환한다`() {
        val request =
            UserResources.Request.BatchRegister(
                emails = listOf(EmbeddedDbInitializer.USER_EMAIL),
                projectId = null,
                authorityDefinitionId = null,
                initialStatus = User.Status.ALLOW,
            )

        val results = flow.bulkRegister(superSession, request)

        results.size shouldBe 1
        results.first().outcome shouldBe UserResources.Reply.BatchRegister.Outcome.SKIPPED
        results.first().userId shouldBe null
    }

    @Test
    fun `ALLOW 상태로 등록하고 권한그룹 선택 시 사용자가 생성되고 권한이 부여된다`() {
        val email = "bulk-with-grant@test.com"
        val request =
            UserResources.Request.BatchRegister(
                emails = listOf(email),
                projectId = projectId,
                authorityDefinitionId = authorityDefinitionId,
                initialStatus = User.Status.ALLOW,
            )

        val results = flow.bulkRegister(superSession, request)

        results.first().outcome shouldBe UserResources.Reply.BatchRegister.Outcome.SUCCESS
        val userId = results.first().userId!!
        val user = userRepository.findById(userId).orElseThrow()
        user.status shouldBe User.Status.ALLOW
        userGrantInteraction.getUserAuthorityGrants(userId, projectId).isNotEmpty() shouldBe true
    }

    @Test
    fun `WAIT 상태로 등록하면 사용자만 생성되고 권한은 부여되지 않는다`() {
        val email = "bulk-wait@test.com"
        val request =
            UserResources.Request.BatchRegister(
                emails = listOf(email),
                projectId = projectId,
                authorityDefinitionId = authorityDefinitionId,
                initialStatus = User.Status.WAIT,
            )

        val results = flow.bulkRegister(superSession, request)

        results.first().outcome shouldBe UserResources.Reply.BatchRegister.Outcome.SUCCESS
        val userId = results.first().userId!!
        val user = userRepository.findById(userId).orElseThrow()
        user.status shouldBe User.Status.WAIT
        userGrantInteraction.getUserAuthorityGrants(userId, projectId).isEmpty() shouldBe true
    }

    @Test
    fun `SUPER 아닌 어드민이 호출하면 401을 반환한다`() {
        adminInteraction.registerBy("regular@bulk-test.com", "pw", "레귤러", null, null, AdminRole.REGULAR, null)
        val regularSession = flow.login("regular@bulk-test.com", "pw")

        val request =
            UserResources.Request.BatchRegister(
                emails = listOf("some@test.com"),
                projectId = null,
                authorityDefinitionId = null,
                initialStatus = User.Status.ALLOW,
            )

        flow
            .bulkRegisterExpectFail(regularSession, request)
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `이메일 11개를 입력하면 400을 반환한다`() {
        val request =
            UserResources.Request.BatchRegister(
                emails = (1..11).map { "overflow$it@test.com" },
                projectId = null,
                authorityDefinitionId = null,
                initialStatus = User.Status.ALLOW,
            )

        flow
            .bulkRegisterExpectFail(superSession, request)
            .andExpect { status { isBadRequest() } }
    }

    @Test
    fun `이름은 이메일 at 앞부분으로 설정된다`() {
        val email = "john.doe@example.com"
        val request =
            UserResources.Request.BatchRegister(
                emails = listOf(email),
                projectId = null,
                authorityDefinitionId = null,
                initialStatus = User.Status.ALLOW,
            )

        val results = flow.bulkRegister(superSession, request)

        val userId = results.first().userId!!
        val user = userRepository.findById(userId).orElseThrow()
        user.name shouldBe "john.doe"
    }
}

package io.mustelidae.otter.lutrogale.web.domain.admin.api

import io.kotest.matchers.shouldBe
import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import io.mustelidae.otter.lutrogale.utils.toJson
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminInteraction
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminRole
import io.mustelidae.otter.lutrogale.web.domain.admin.repository.AdminRepository
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

internal class AdminManagementControllerTest : FlowTestSupport() {
    @Autowired
    private lateinit var adminInteraction: AdminInteraction

    @Autowired
    private lateinit var adminRepository: AdminRepository

    private lateinit var flow: AdminManagementControllerFlow
    private lateinit var superSession: Cookie

    @BeforeEach
    fun setUp() {
        flow = AdminManagementControllerFlow(mockMvc)
        superSession = flow.login("admin@osori.com", "admin")
    }

    @Test
    fun `SUPER 어드민은 활성 어드민 목록을 조회할 수 있다`() {
        val admins = flow.findAllAdmins(superSession)
        admins.isEmpty() shouldBe false
    }

    @Test
    fun `SUPER 어드민이 신규 어드민을 생성하면 즉시 활성 상태이다`() {
        val request =
            AdminResources.Request.Create(
                email = "new-admin@test.com",
                name = "신규어드민",
                pw = "password123",
                role = AdminRole.REGULAR,
            )

        val newId = flow.createAdmin(superSession, request)

        val admin = adminRepository.findById(newId).orElseThrow()
        admin.status shouldBe true
        admin.role shouldBe AdminRole.REGULAR
    }

    @Test
    fun `REGULAR 어드민은 신규 어드민을 생성할 수 없다`() {
        adminInteraction.registerBy(
            "regular@test.com",
            "pw",
            "레귤러",
            null,
            null,
            AdminRole.REGULAR,
            null,
        )
        val regularSession = flow.login("regular@test.com", "pw")

        val request =
            AdminResources.Request.Create(
                email = "another@test.com",
                name = "다른어드민",
                pw = "password",
                role = AdminRole.REGULAR,
            )

        flow
            .createAdminExpectFail(regularSession, request)
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `SUPER 어드민은 다른 어드민을 만료시킬 수 있다`() {
        val targetId =
            adminInteraction.registerBy(
                "expire-target@test.com",
                "pw",
                "만료대상",
                null,
                null,
                AdminRole.REGULAR,
                null,
            )

        flow
            .expireAdmin(superSession, targetId)
            .andExpect { status { isCreated() } }

        val expiredAdmin = adminRepository.findById(targetId).orElseThrow()
        expiredAdmin.status shouldBe false
    }

    @Test
    fun `SUPER 어드민은 본인 계정을 만료시킬 수 없다`() {
        val superAdminId = adminRepository.findAll().first { it.role == AdminRole.SUPER }.id!!
        flow
            .expireAdmin(superSession, superAdminId)
            .andExpect { status { isBadRequest() } }
    }

    @Test
    fun `REGULAR 어드민은 다른 어드민을 만료시킬 수 없다`() {
        adminInteraction.registerBy(
            "regular2@test.com",
            "pw",
            "레귤러2",
            null,
            null,
            AdminRole.REGULAR,
            null,
        )
        val targetId =
            adminInteraction.registerBy(
                "target2@test.com",
                "pw",
                "만료대상2",
                null,
                null,
                AdminRole.REGULAR,
                null,
            )
        val regularSession = flow.login("regular2@test.com", "pw")

        flow
            .expireAdmin(regularSession, targetId)
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `SUPER 어드민은 다른 어드민의 비밀번호를 변경할 수 있다`() {
        val targetId =
            adminInteraction.registerBy(
                "pw-change@test.com",
                "oldpw",
                "PW변경대상",
                null,
                null,
                AdminRole.REGULAR,
                null,
            )

        flow
            .changePassword(superSession, targetId, "newpw123")
            .andExpect { status { isOk() } }
    }

    @Test
    fun `REGULAR 어드민은 본인 비밀번호만 변경할 수 있다`() {
        val regularId =
            adminInteraction.registerBy(
                "self-pw@test.com",
                "oldpw",
                "본인PW변경",
                null,
                null,
                AdminRole.REGULAR,
                null,
            )
        val regularSession = flow.login("self-pw@test.com", "oldpw")

        flow
            .changePassword(regularSession, regularId, "newpw456")
            .andExpect { status { isOk() } }
    }

    @Test
    fun `REGULAR 어드민은 타인의 비밀번호를 변경할 수 없다`() {
        adminInteraction.registerBy(
            "regular3@test.com",
            "pw",
            "레귤러3",
            null,
            null,
            AdminRole.REGULAR,
            null,
        )
        val otherAdminId =
            adminInteraction.registerBy(
                "other@test.com",
                "pw",
                "다른어드민",
                null,
                null,
                AdminRole.REGULAR,
                null,
            )
        val regularSession = flow.login("regular3@test.com", "pw")

        flow
            .changePassword(regularSession, otherAdminId, "hacked")
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `만료된 어드민은 로그인할 수 없다`() {
        val targetId =
            adminInteraction.registerBy(
                "expired-login@test.com",
                "pw",
                "만료로그인",
                null,
                null,
                AdminRole.REGULAR,
                null,
            )
        flow
            .expireAdmin(superSession, targetId)
            .andExpect { status { isCreated() } }

        mockMvc
            .post("/v1/check-login") {
                contentType = MediaType.APPLICATION_JSON
                content = mapOf("email" to "expired-login@test.com", "password" to "pw").toJson()
            }.andExpect { status { isBadRequest() } }
    }

    @Test
    fun `만료된 어드민은 목록에 포함되지 않는다`() {
        val targetId =
            adminInteraction.registerBy(
                "expired@test.com",
                "pw",
                "만료어드민",
                null,
                null,
                AdminRole.REGULAR,
                null,
            )

        flow
            .expireAdmin(superSession, targetId)
            .andExpect { status { isCreated() } }

        val admins = flow.findAllAdmins(superSession)
        admins.none { it.id == targetId } shouldBe true
    }
}

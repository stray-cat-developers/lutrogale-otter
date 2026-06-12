package io.mustelidae.otter.lutrogale.api.domain.user.api

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import io.mustelidae.otter.lutrogale.web.domain.project.repository.ProjectRepository
import io.mustelidae.otter.lutrogale.web.domain.user.User
import io.mustelidae.otter.lutrogale.web.domain.user.UserInteraction
import io.mustelidae.otter.lutrogale.web.domain.user.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserManagementControllerTest : FlowTestSupport() {
    @Autowired private lateinit var projectRepository: ProjectRepository

    @Autowired private lateinit var userRepository: UserRepository

    @Autowired private lateinit var userInteraction: UserInteraction

    @Test
    fun `단건 이메일로 사용자를 만료시킨다`() {
        val email = "expire-single@test.com"
        userInteraction.createBy(email, "Test User", User.Status.ALLOW)
        val flow = UserManagementControllerFlow(projectRepository, mockMvc)

        flow.expire(email)

        val user = userRepository.findByEmail(email)
        user shouldNotBe null
        user!!.status shouldBe User.Status.EXPIRE
    }

    @Test
    fun `존재하지 않는 이메일 단건 만료는 4xx를 반환한다`() {
        val flow = UserManagementControllerFlow(projectRepository, mockMvc)

        flow.expireExpectingError("nonexistent@test.com")
    }

    @Test
    fun `벌크 만료에서 존재하지 않는 이메일은 무시하고 존재하는 사용자만 만료시킨다`() {
        val existingEmail = "expire-bulk@test.com"
        val nonExistentEmail = "bulk-notfound@test.com"
        userInteraction.createBy(existingEmail, "Bulk User", User.Status.ALLOW)
        val flow = UserManagementControllerFlow(projectRepository, mockMvc)

        flow.expireBulk(listOf(existingEmail, nonExistentEmail))

        val user = userRepository.findByEmail(existingEmail)
        user shouldNotBe null
        user!!.status shouldBe User.Status.EXPIRE
        userRepository.findByEmail(nonExistentEmail) shouldBe null
    }
}

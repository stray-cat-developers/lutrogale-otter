package io.mustelidae.otter.lutrogale.api.domain.authorization.api

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.mustelidae.otter.lutrogale.api.config.EmbeddedDbInitializer
import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import io.mustelidae.otter.lutrogale.web.domain.project.repository.ProjectRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMethod

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AuthorizationControllerTest : FlowTestSupport() {

    @Autowired private lateinit var projectRepository: ProjectRepository

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

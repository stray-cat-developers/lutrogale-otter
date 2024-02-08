package io.mustelidae.otter.lutrogale.api.domain.authorization.api

import io.mustelidae.otter.lutrogale.api.permission.RoleHeader
import io.mustelidae.otter.lutrogale.common.Replies
import io.mustelidae.otter.lutrogale.utils.fromJson
import io.mustelidae.otter.lutrogale.utils.toJson
import io.mustelidae.otter.lutrogale.web.domain.project.repository.ProjectRepository
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.web.bind.annotation.RequestMethod

class AuthorizationControllerFlow(
    projectRepository: ProjectRepository,
    private val mockMvc: MockMvc
) {

    private val apiKey: String = projectRepository.findAll().first().apiKey

    fun idCheck(email: String, ids: List<Long>): List<AccessResources.Reply.AccessState> {
        val request = AccessResources.Request.IdBase(email, ids)
        val uri = linkTo<AuthorizationController> { idChecks(apiKey, request) }.toUri()

        return mockMvc.post(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XSystem.KEY, apiKey)
            content = request.toJson()
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Replies<AccessResources.Reply.AccessState>>()
            .getContent()
            .toList()
    }

    fun uriCheck(email: String, url: String, method: RequestMethod): List<AccessResources.Reply.AccessState> {
        val request = AccessResources.Request.UriBase(
            email,
            listOf(
                AccessResources.AccessUri(url, method)
            )
        )

        val uri = linkTo<AuthorizationController> { urlCheck(apiKey, request) }.toUri()

        return mockMvc.post(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XSystem.KEY, apiKey)
            content = request.toJson()
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Replies<AccessResources.Reply.AccessState>>()
            .getContent()
            .toList()
    }

    fun findAllAccessibleGrant(email: String): List<AccessResources.AccessUri> {
        val uri = linkTo<AuthorizationController> { findAllAccessibleGrant(apiKey, email) }.toUri()

        return mockMvc.get(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XSystem.KEY, apiKey)
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Replies<AccessResources.AccessUri>>()
            .getContent()
            .toList()
    }
}

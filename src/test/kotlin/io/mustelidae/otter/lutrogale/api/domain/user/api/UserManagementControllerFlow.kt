package io.mustelidae.otter.lutrogale.api.domain.user.api

import io.mustelidae.otter.lutrogale.api.permission.RoleHeader
import io.mustelidae.otter.lutrogale.utils.toJson
import io.mustelidae.otter.lutrogale.web.domain.project.repository.ProjectRepository
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

class UserManagementControllerFlow(
    projectRepository: ProjectRepository,
    private val mockMvc: MockMvc,
) {
    private val apiKey: String = projectRepository.findAll().first().apiKey

    fun expire(email: String) {
        val body = UserManagementResources.Request.Expire(email)
        mockMvc
            .post("/v1/users/expire") {
                contentType = MediaType.APPLICATION_JSON
                header(RoleHeader.XSystem.KEY, apiKey)
                content = body.toJson()
            }.andExpect {
                status { isNoContent() }
            }
    }

    fun expireExpectingError(email: String) {
        val body = UserManagementResources.Request.Expire(email)
        mockMvc
            .post("/v1/users/expire") {
                contentType = MediaType.APPLICATION_JSON
                header(RoleHeader.XSystem.KEY, apiKey)
                content = body.toJson()
            }.andExpect {
                status { is4xxClientError() }
            }
    }

    fun expireBulk(emails: List<String>) {
        val body = UserManagementResources.Request.BulkExpire(emails)
        mockMvc
            .post("/v1/users/expire/bulk") {
                contentType = MediaType.APPLICATION_JSON
                header(RoleHeader.XSystem.KEY, apiKey)
                content = body.toJson()
            }.andExpect {
                status { isNoContent() }
            }
    }
}

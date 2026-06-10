package io.mustelidae.otter.lutrogale.web.domain.admin.api

import io.mustelidae.otter.lutrogale.common.Replies
import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.utils.fromJson
import io.mustelidae.otter.lutrogale.utils.toJson
import jakarta.servlet.http.Cookie
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

internal class AdminManagementControllerFlow(
    private val mockMvc: MockMvc,
) {
    fun login(
        email: String,
        password: String,
    ): Cookie {
        val body = mapOf("email" to email, "password" to password)
        val response =
            mockMvc
                .post("/v1/check-login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = body.toJson()
                }.andExpect {
                    status { is2xxSuccessful() }
                }.andReturn()
                .response

        return response.getCookie("SESSION")
            ?: error("SESSION cookie not found after login")
    }

    fun findAllAdmins(session: Cookie): List<AdminResources.AdminRow> =
        mockMvc
            .get("/v1/maintenance/management/admins") {
                contentType = MediaType.APPLICATION_JSON
                cookie(session)
            }.andExpect {
                status { is2xxSuccessful() }
            }.andReturn()
            .response
            .contentAsString
            .fromJson<Replies<AdminResources.AdminRow>>()
            .getContent()
            .toList()

    fun createAdmin(
        session: Cookie,
        request: AdminResources.Request.Create,
    ): Long =
        mockMvc
            .post("/v1/maintenance/management/admins") {
                contentType = MediaType.APPLICATION_JSON
                cookie(session)
                content = request.toJson()
            }.andExpect {
                status { isCreated() }
            }.andReturn()
            .response
            .contentAsString
            .fromJson<Reply<Long>>()
            .content!!

    fun createAdminExpectFail(
        session: Cookie,
        request: AdminResources.Request.Create,
    ): ResultActionsDsl =
        mockMvc.post("/v1/maintenance/management/admins") {
            contentType = MediaType.APPLICATION_JSON
            cookie(session)
            content = request.toJson()
        }

    fun expireAdmin(
        session: Cookie,
        adminId: Long,
    ): ResultActionsDsl =
        mockMvc.post("/v1/maintenance/management/admin/$adminId/expire") {
            contentType = MediaType.APPLICATION_JSON
            cookie(session)
        }

    fun changePassword(
        session: Cookie,
        adminId: Long,
        newPw: String,
    ): ResultActionsDsl =
        mockMvc.put("/v1/maintenance/management/admin/$adminId/pw") {
            contentType = MediaType.APPLICATION_JSON
            cookie(session)
            content = AdminResources.Request.PasswordChange(newPw).toJson()
        }
}

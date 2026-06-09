package io.mustelidae.otter.lutrogale.web.domain.user.api

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

internal class UserBulkRegisterControllerFlow(private val mockMvc: MockMvc) {

    fun login(email: String, password: String): Cookie {
        val body = mapOf("email" to email, "password" to password)
        val response = mockMvc.post("/v1/check-login") {
            contentType = MediaType.APPLICATION_JSON
            content = body.toJson()
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn().response

        return response.getCookie("SESSION")
            ?: error("SESSION cookie not found after login")
    }

    fun bulkRegister(session: Cookie, request: UserResources.BatchRegister.Request): List<UserResources.BatchRegister.Result> {
        return mockMvc.post("/v1/maintenance/management/user/batch") {
            contentType = MediaType.APPLICATION_JSON
            cookie(session)
            content = request.toJson()
        }.andExpect {
            status { isCreated() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Replies<UserResources.BatchRegister.Result>>()
            .getContent()
            .toList()
    }

    fun bulkRegisterExpectFail(session: Cookie, request: UserResources.BatchRegister.Request): ResultActionsDsl {
        return mockMvc.post("/v1/maintenance/management/user/batch") {
            contentType = MediaType.APPLICATION_JSON
            cookie(session)
            content = request.toJson()
        }
    }

    fun findUserDetail(session: Cookie, userId: Long): UserResources.Reply.Detail {
        return mockMvc.get("/v1/maintenance/management/user/$userId") {
            cookie(session)
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Reply<UserResources.Reply.Detail>>()
            .content!!
    }
}

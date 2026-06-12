package io.mustelidae.otter.lutrogale.web.domain.project.api

import io.mustelidae.otter.lutrogale.common.Replies
import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.utils.fromJson
import io.mustelidae.otter.lutrogale.utils.toJson
import jakarta.servlet.http.Cookie
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

internal class ProjectControllerFlow(
    private val mockMvc: MockMvc,
) {
    fun findAll(session: Cookie): List<ProjectResources.Reply> =
        mockMvc
            .get("/v1/maintenance/projects") {
                contentType = MediaType.APPLICATION_JSON
                cookie(session)
            }.andExpect {
                status { isOk() }
            }.andReturn()
            .response
            .contentAsString
            .fromJson<Replies<ProjectResources.Reply>>()
            .getContent()
            .toList()

    fun getSyncInfo(
        session: Cookie,
        projectId: Long,
    ): ProjectResources.Reply.SyncInfo =
        mockMvc
            .get("/v1/maintenance/project/$projectId/sync") {
                contentType = MediaType.APPLICATION_JSON
                cookie(session)
            }.andExpect {
                status { isOk() }
            }.andReturn()
            .response
            .contentAsString
            .fromJson<Reply<ProjectResources.Reply.SyncInfo>>()
            .content!!

    fun getSyncInfoExpectStatus(
        session: Cookie,
        projectId: Long,
    ): ResultActionsDsl =
        mockMvc.get("/v1/maintenance/project/$projectId/sync") {
            contentType = MediaType.APPLICATION_JSON
            cookie(session)
        }

    fun registerSync(
        session: Cookie,
        projectId: Long,
        request: ProjectResources.Request.RegisterSync,
    ) {
        mockMvc
            .post("/v1/maintenance/project/$projectId/sync") {
                contentType = MediaType.APPLICATION_JSON
                cookie(session)
                content = request.toJson()
            }.andExpect {
                status { isCreated() }
            }
    }

    fun updateSync(
        session: Cookie,
        projectId: Long,
        request: ProjectResources.Modify.UpdateSync,
    ) {
        mockMvc
            .put("/v1/maintenance/project/$projectId/sync") {
                contentType = MediaType.APPLICATION_JSON
                cookie(session)
                content = request.toJson()
            }.andExpect {
                status { isOk() }
            }
    }

    fun deleteSync(
        session: Cookie,
        projectId: Long,
    ) {
        mockMvc
            .delete("/v1/maintenance/project/$projectId/sync") {
                contentType = MediaType.APPLICATION_JSON
                cookie(session)
            }.andExpect {
                status { isNoContent() }
            }
    }
}

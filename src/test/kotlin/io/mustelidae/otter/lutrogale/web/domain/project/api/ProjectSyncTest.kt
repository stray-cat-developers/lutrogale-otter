package io.mustelidae.otter.lutrogale.web.domain.project.api

import io.kotest.matchers.shouldBe
import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import io.mustelidae.otter.lutrogale.utils.toJson
import io.mustelidae.otter.lutrogale.web.domain.admin.api.AdminManagementControllerFlow
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectInteraction
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

internal class ProjectSyncTest : FlowTestSupport() {
    @Autowired
    private lateinit var projectInteraction: ProjectInteraction

    private lateinit var adminFlow: AdminManagementControllerFlow
    private lateinit var flow: ProjectControllerFlow
    private lateinit var superSession: Cookie
    private var testProjectId: Long = 0L

    @BeforeEach
    fun setUp() {
        adminFlow = AdminManagementControllerFlow(mockMvc)
        flow = ProjectControllerFlow(mockMvc)
        superSession = adminFlow.login("admin@osori.com", "admin")
        testProjectId = projectInteraction.register("Sync Test Project", "for sync testing", null)
    }

    @Test
    fun `자동 Sync를 등록하면 조회 시 동일한 specType과 url을 반환한다`() {
        val request =
            ProjectResources.Request.RegisterSync(
                specType = Project.SpecType.OPENAPI_JSON,
                url = "http://example.com/openapi.json",
            )
        flow.registerSync(superSession, testProjectId, request)

        val syncInfo = flow.getSyncInfo(superSession, testProjectId)
        syncInfo.specType shouldBe Project.SpecType.OPENAPI_JSON
        syncInfo.url shouldBe "http://example.com/openapi.json"
    }

    @Test
    fun `자동 Sync를 업데이트하면 조회 시 변경된 정보를 반환한다`() {
        flow.registerSync(
            superSession,
            testProjectId,
            ProjectResources.Request.RegisterSync(
                specType = Project.SpecType.OPENAPI_JSON,
                url = "http://example.com/v1.json",
            ),
        )

        flow.updateSync(
            superSession,
            testProjectId,
            ProjectResources.Modify.UpdateSync(
                specType = Project.SpecType.OPENAPI_YAML,
                url = "http://example.com/v2.yaml",
            ),
        )

        val syncInfo = flow.getSyncInfo(superSession, testProjectId)
        syncInfo.specType shouldBe Project.SpecType.OPENAPI_YAML
        syncInfo.url shouldBe "http://example.com/v2.yaml"
    }

    @Test
    fun `자동 Sync를 삭제하면 조회 시 404가 반환된다`() {
        flow.registerSync(
            superSession,
            testProjectId,
            ProjectResources.Request.RegisterSync(
                specType = Project.SpecType.GRAPHQL,
                url = "http://example.com/graphql",
            ),
        )
        flow.deleteSync(superSession, testProjectId)

        flow
            .getSyncInfoExpectStatus(superSession, testProjectId)
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `Sync가 등록된 프로젝트는 전체 조회 시 syncEnabled가 true이다`() {
        flow.registerSync(
            superSession,
            testProjectId,
            ProjectResources.Request.RegisterSync(
                specType = Project.SpecType.OPENAPI_JSON,
                url = "http://example.com/openapi.json",
            ),
        )

        val projects = flow.findAll(superSession)
        val project = projects.first { it.id == testProjectId }
        project.syncEnabled shouldBe true
        project.specType shouldBe Project.SpecType.OPENAPI_JSON
        project.migrationUrl shouldBe "http://example.com/openapi.json"
    }

    @Test
    fun `Sync가 없는 프로젝트는 전체 조회 시 syncEnabled가 false이다`() {
        val projects = flow.findAll(superSession)
        val project = projects.first { it.id == testProjectId }
        project.syncEnabled shouldBe false
    }

    @Test
    fun `이미 Sync가 등록된 프로젝트에 등록 시도하면 412가 반환된다`() {
        flow.registerSync(
            superSession,
            testProjectId,
            ProjectResources.Request.RegisterSync(
                specType = Project.SpecType.OPENAPI_JSON,
                url = "http://example.com/openapi.json",
            ),
        )

        mockMvc
            .post("/v1/maintenance/project/$testProjectId/sync") {
                contentType = MediaType.APPLICATION_JSON
                cookie(superSession)
                content =
                    ProjectResources.Request
                        .RegisterSync(
                            specType = Project.SpecType.OPENAPI_JSON,
                            url = "http://example.com/duplicate.json",
                        ).toJson()
            }.andExpect { status { isPreconditionFailed() } }
    }

    @Test
    fun `Sync가 없는 프로젝트에 DELETE 요청하면 204가 반환된다`() {
        flow.deleteSync(superSession, testProjectId)
    }
}

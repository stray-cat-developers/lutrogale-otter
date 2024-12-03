package io.mustelidae.otter.lutrogale.api.domain.migration.api

import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.utils.fromJson
import io.mustelidae.otter.lutrogale.utils.toJson
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class APISpecMigrationControllerFlow(
    private val mockMvc: MockMvc,
) {

    fun previewOpenAPI(url: String, migrationType: MigrationResources.Request.OpenAPI.MigrationType): String {
        val request = MigrationResources.Request.OpenAPI(
            uri = url,
            version = "2.0.0",
            format = MigrationResources.Request.OpenAPI.OpenAPIFormat.JSON,
            migrationType = migrationType,
        )
        val uri = linkTo<APISpecMigrationController> { previewOpenAPI(request) }.toUri()

        return mockMvc.post(uri) {
            contentType = MediaType.APPLICATION_JSON
            content = request.toJson()
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Reply<String>>()
            .content!!
    }

    fun generateOpenAPI(projectId: Long, url: String, migrationType: MigrationResources.Request.OpenAPI.MigrationType): Long {
        val request = MigrationResources.Request.OpenAPI(
            uri = url,
            version = "2.0.0",
            format = MigrationResources.Request.OpenAPI.OpenAPIFormat.JSON,
            migrationType = migrationType,
        )
        val uri = linkTo<APISpecMigrationController> { generateOpenAPI(projectId, request) }.toUri()

        return mockMvc.post(uri) {
            contentType = MediaType.APPLICATION_JSON
            content = request.toJson()
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Reply<Long>>()
            .content!!
    }
}

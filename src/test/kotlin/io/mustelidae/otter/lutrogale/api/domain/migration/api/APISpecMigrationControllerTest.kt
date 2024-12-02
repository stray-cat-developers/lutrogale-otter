package io.mustelidae.otter.lutrogale.api.domain.migration.api

import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import io.mustelidae.otter.lutrogale.web.domain.project.api.ProjectController
import io.mustelidae.otter.lutrogale.web.domain.project.api.ProjectResources
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class APISpecMigrationControllerTest : FlowTestSupport() {

    @Autowired
    lateinit var projectController: ProjectController

    var projectId: Long = 0L

    @BeforeAll
    fun beforeAll() {
        projectId = projectController.create(
            ProjectResources.Request(
                name = "migration",
                description = "migration test",
            ),
        ).content!!
    }

    @Test
    fun previewOpenAPI() {
        val flow = APISpecMigrationControllerFlow(mockMvc)
        val preview = flow.previewOpenAPI("https://petstore.swagger.io/v2/swagger.json", MigrationResources.Request.OpenAPI.MigrationType.TREE)

        println(preview)
    }

    @Test
    fun generateOpenAPI() {
    }
}

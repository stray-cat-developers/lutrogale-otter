package io.mustelidae.otter.lutrogale.api.domain.migration.api

import io.kotest.matchers.shouldBe
import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import io.mustelidae.otter.lutrogale.api.domain.migration.graphql.HttpOperation
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
    fun previewOpenAPIUsingTree() {
        val flow = APISpecMigrationControllerFlow(mockMvc)
        val preview = flow.previewOpenAPI(
            "https://petstore.swagger.io/v2/swagger.json",
            MigrationResources.Request.OpenAPI.MigrationType.TREE,
        )
        println(preview)
    }

    @Test
    fun generateOpenAPIUsingTree() {
        val flow = APISpecMigrationControllerFlow(mockMvc)
        val preview = flow.generateOpenAPI(projectId, "https://petstore.swagger.io/v2/swagger.json", MigrationResources.Request.OpenAPI.MigrationType.TREE)
        println(preview)
    }

    @Test
    fun previewOpenAPIUsingFlat() {
        val flow = APISpecMigrationControllerFlow(mockMvc)
        val preview = flow.previewOpenAPI("https://petstore.swagger.io/v2/swagger.json", MigrationResources.Request.OpenAPI.MigrationType.FLAT)

        preview shouldBe """
            /pet PUT
            /pet POST
            /pet/findByStatus GET
            /pet/findByTags GET
            /pet/{petId} GET
            /pet/{petId} POST
            /pet/{petId} DELETE
            /pet/{petId}/uploadImage POST
            /store/inventory GET
            /store/order POST
            /store/order/{orderId} GET
            /store/order/{orderId} DELETE
            /user POST
            /user/createWithArray POST
            /user/createWithList POST
            /user/login GET
            /user/logout GET
            /user/{username} GET
            /user/{username} PUT
            /user/{username} DELETE
            
        """.trimIndent()
    }

    @Test
    fun generateOpenAPIUsingFlat() {
        val flow = APISpecMigrationControllerFlow(mockMvc)
        flow.generateOpenAPI(projectId, "https://petstore.swagger.io/v2/swagger.json", MigrationResources.Request.OpenAPI.MigrationType.FLAT)
    }

    @Test
    fun previewGraphQL() {
        val flow = APISpecMigrationControllerFlow(mockMvc)
        val preview = flow.previewGraphQL("https://api.spacex.land/graphql", HttpOperation.GET_AND_POST)
        println(preview)

        preview.replace("\n", "") shouldBe """
            Tweet GET
            Tweets GET
            TweetsMeta GET
            User GET
            Notifications GET
            NotificationsMeta GET
            createTweet POST
            deleteTweet POST
            markTweetRead POST
        """.trimIndent().replace("\n", "")
    }

    @Test
    fun generateGraphQL() {
        val flow = APISpecMigrationControllerFlow(mockMvc)
        val menuId = flow.generateGraphQL(projectId, "https://api.spacex.land/graphql", HttpOperation.GET_AND_POST)
        println(menuId)
    }
}

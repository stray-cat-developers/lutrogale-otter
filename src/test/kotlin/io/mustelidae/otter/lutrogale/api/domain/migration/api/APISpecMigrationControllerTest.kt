package io.mustelidae.otter.lutrogale.api.domain.migration.api

import io.kotest.matchers.shouldBe
import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import io.mustelidae.otter.lutrogale.api.domain.migration.graphql.HttpOperation
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import io.mustelidae.otter.lutrogale.web.domain.project.api.ProjectController
import io.mustelidae.otter.lutrogale.web.domain.project.api.ProjectResources
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional(readOnly = true)
class APISpecMigrationControllerTest : FlowTestSupport() {

    @Autowired
    lateinit var projectController: ProjectController

    @Autowired
    lateinit var projectFinder: ProjectFinder

    var projectId: Long = 0L
    val whiteSpace = "\\s".toRegex()

    @BeforeEach
    fun beforeEach() {
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

        preview.replace(whiteSpace, "") shouldBe """
            - /pet (GET)
                - /findByStatus (GET)
                - /findByTags (GET)
                - /{petId} (GET)
                    - /uploadImage (GET)
                    - /uploadImage (POST)
                - /{petId} (POST)
                - /{petId} (DELETE)
            - /pet (PUT)
            - /pet (POST)
            - /store (GET)
                - /inventory (GET)
                - /order (GET)
                    - /{orderId} (GET)
                    - /{orderId} (DELETE)
                - /order (POST)
            - /user (GET)
                - /createWithArray (GET)
                - /createWithArray (POST)
                - /createWithList (GET)
                - /createWithList (POST)
                - /login (GET)
                - /logout (GET)
                - /{username} (GET)
                - /{username} (PUT)
                - /{username} (DELETE)
            - /user (POST)            
        """.trimIndent()
            .replace(whiteSpace, "")
    }

    @Test
    fun generateOpenAPIUsingTree() {
        val flow = APISpecMigrationControllerFlow(mockMvc)
        flow.generateOpenAPI(projectId, "https://petstore.swagger.io/v2/swagger.json", MigrationResources.Request.OpenAPI.MigrationType.TREE)
        val project = projectFinder.findBy(projectId)
        project.menuNavigations.size shouldBe 28
    }

    @Test
    fun previewOpenAPIUsingFlat() {
        val flow = APISpecMigrationControllerFlow(mockMvc)
        val preview = flow.previewOpenAPI("https://petstore.swagger.io/v2/swagger.json", MigrationResources.Request.OpenAPI.MigrationType.FLAT)

        preview.replace(whiteSpace, "") shouldBe """
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
            .replace(whiteSpace, "")
    }

    @Test
    fun generateOpenAPIUsingFlat() {
        val flow = APISpecMigrationControllerFlow(mockMvc)
        flow.generateOpenAPI(projectId, "https://petstore.swagger.io/v2/swagger.json", MigrationResources.Request.OpenAPI.MigrationType.FLAT)
        val project = projectFinder.findBy(projectId)
        project.menuNavigations.size shouldBe 21
    }

    @Test
    fun previewGraphQL() {
        val flow = APISpecMigrationControllerFlow(mockMvc)
        val preview = flow.previewGraphQL("https://api.spacex.land/graphql", HttpOperation.GET_AND_POST)

        preview.replace(whiteSpace, "") shouldBe """
            Tweet GET
            Tweets GET
            TweetsMeta GET
            User GET
            Notifications GET
            NotificationsMeta GET
            createTweet POST
            deleteTweet POST
            markTweetRead POST
        """.trimIndent()
            .replace(whiteSpace, "")
    }

    @Test
    fun generateGraphQL() {
        val flow = APISpecMigrationControllerFlow(mockMvc)
        flow.generateGraphQL(projectId, "https://api.spacex.land/graphql", HttpOperation.GET_AND_POST)
        val project = projectFinder.findBy(projectId)
        project.menuNavigations.size shouldBe 10
    }
}

package io.mustelidae.otter.lutrogale.web.domain.project.api

import io.mustelidae.otter.lutrogale.utils.toDateString
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation.ListStructure
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

class ProjectResources {
    class Request {
        @Schema(name = "Lutrogale.Project.Request.Create")
        data class Create(
            val name: String,
            val description: String,
            val listStructure: ListStructure? = null,
        )

        @Schema(name = "Lutrogale.Project.Request.RegisterSync")
        data class RegisterSync(
            val specType: Project.SpecType,
            @field:NotBlank val url: String,
        )
    }

    class Modify {
        @Schema(name = "Lutrogale.Project.Modify.UpdateSync")
        data class UpdateSync(
            val specType: Project.SpecType,
            @field:NotBlank val url: String,
        )
    }

    @Schema(name = "Lutrogale.Project.Reply")
    class Reply(
        val id: Long,
        val name: String,
        val apiKey: String,
        val created: String,
        val status: Boolean,
        val description: String? = null,
        val syncEnabled: Boolean = false,
        val specType: Project.SpecType? = null,
        val migrationUrl: String? = null,
    ) {
        companion object {
            fun from(project: Project): Reply =
                project.run {
                    Reply(
                        id!!,
                        name,
                        apiKey,
                        createdAt!!.toDateString(),
                        status,
                        description,
                        syncEnabled,
                        specType,
                        migrationUrl,
                    )
                }
        }

        @Schema(name = "Lutrogale.Project.Reply.SyncInfo")
        data class SyncInfo(
            val specType: Project.SpecType,
            val url: String,
        ) {
            companion object {
                fun from(project: Project): SyncInfo =
                    SyncInfo(
                        specType = project.specType!!,
                        url = project.migrationUrl!!,
                    )
            }
        }
    }
}

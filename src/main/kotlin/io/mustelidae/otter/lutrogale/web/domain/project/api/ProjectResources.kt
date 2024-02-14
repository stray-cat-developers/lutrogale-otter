package io.mustelidae.otter.lutrogale.web.domain.project.api

import io.mustelidae.otter.lutrogale.utils.toDateString
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.swagger.v3.oas.annotations.media.Schema

class ProjectResources {

    @Schema(name = "Lutrogale.Project.Request")
    data class Request(
        val name: String,
        val description: String,
    )

    @Schema(name = "Lutrogale.Project.Reply")
    class Reply(
        val id: Long,
        val name: String,
        val apiKey: String,
        val created: String,
        val status: Boolean,
        val description: String? = null,
    ) {

        companion object {

            fun from(project: Project): Reply {
                return project.run {
                    Reply(
                        id!!,
                        name,
                        apiKey,
                        createdAt!!.toDateString(),
                        status,
                        description,
                    )
                }
            }
        }
    }
}

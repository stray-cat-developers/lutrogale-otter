package io.mustelidae.otter.lutrogale.web.domain.project.api

import io.mustelidae.otter.lutrogale.utils.toDateString
import io.mustelidae.otter.lutrogale.web.domain.project.Project

class ProjectResources {

    data class Request(
        val name: String,
        val description: String,
    )

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

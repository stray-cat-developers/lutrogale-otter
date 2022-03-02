package io.mustelidae.otter.lutrogale.web.domain.admin.api

import io.mustelidae.otter.lutrogale.web.domain.admin.Admin
import io.swagger.v3.oas.annotations.media.Schema

class AdminResources {

    @Schema(name = "Admin.Modify")
    class Modify(
        val description: String,
        val imageUrl: String,
        val pw: String? = null
    )

    @Schema(name = "Admin.Reply")
    class Reply(
        val id: Long,
        val email: String,
        val name: String,
        val description: String? = null,
        val img: String? = null
    ) {

        companion object {

            fun from(admin: Admin): Reply {
                return admin.run {
                    Reply(
                        id!!,
                        email,
                        name,
                        description,
                        img
                    )
                }
            }
        }
    }
}

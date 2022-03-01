package io.mustelidae.otter.lutrogale.web.domain.admin.api

import io.mustelidae.otter.lutrogale.web.domain.admin.Admin

class AdminResources {

    class Modify(
        val description: String,
        val imageUrl: String,
        val pw: String? = null
    )

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


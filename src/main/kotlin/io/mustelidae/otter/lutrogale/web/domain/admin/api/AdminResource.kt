package io.mustelidae.otter.lutrogale.web.domain.admin.api

import io.mustelidae.otter.lutrogale.web.domain.admin.Admin

class AdminResource(
    val id: Long,
    val email: String,
    val name: String,
    val description: String? = null,
    val img: String? = null
) {

    companion object {

        fun of(admin: Admin): AdminResource {
            return admin.run {
                AdminResource(
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

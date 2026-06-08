package io.mustelidae.otter.lutrogale.web.domain.admin.api

import io.mustelidae.otter.lutrogale.web.domain.admin.Admin
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminRole
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class AdminResources {

    @Schema(name = "Lutrogale.Admin.Modify")
    class Modify(
        val description: String,
        val imageUrl: String,
        val pw: String? = null,
    )

    @Schema(name = "Lutrogale.Admin.Reply")
    class Reply(
        val id: Long,
        val email: String,
        val name: String,
        val description: String? = null,
        val img: String? = null,
        val role: AdminRole,
    ) {

        companion object {

            fun from(admin: Admin): Reply {
                return admin.run {
                    Reply(
                        id!!,
                        email,
                        name,
                        description,
                        img,
                        role,
                    )
                }
            }
        }
    }

    class Request {

        @Schema(name = "Lutrogale.Admin.Request.Create")
        class Create(
            val email: String,
            val name: String,
            val pw: String,
            val role: AdminRole,
            val description: String? = null,
            val parentAdminId: Long? = null,
        )

        @Schema(name = "Lutrogale.Admin.Request.PasswordChange")
        class PasswordChange(
            val pw: String,
        )
    }

    @Schema(name = "Lutrogale.Admin.AdminRow")
    data class AdminRow(
        val id: Long,
        val email: String,
        val name: String,
        val role: AdminRole,
        val description: String?,
        val createdAt: LocalDateTime,
        val parentAdminId: Long?,
        val children: List<AdminRow>,
    ) {
        companion object {
            fun from(admin: Admin): AdminRow {
                return AdminRow(
                    id = admin.id!!,
                    email = admin.email,
                    name = admin.name,
                    role = admin.role,
                    description = admin.description,
                    createdAt = admin.createdAt!!,
                    parentAdminId = admin.parentAdmin?.id,
                    children = admin.admins.map { from(it) },
                )
            }
        }
    }
}

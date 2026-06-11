package io.mustelidae.otter.lutrogale.web.domain.admin.api

import io.mustelidae.otter.lutrogale.web.domain.admin.Admin
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminRole
import java.time.LocalDateTime

class AdminResources {
    class Modify(
        val description: String,
        val imageUrl: String,
        val pw: String? = null,
    )

    class Reply(
        val id: Long,
        val email: String,
        val name: String,
        val description: String? = null,
        val img: String? = null,
        val role: AdminRole,
    ) {
        companion object {
            fun from(admin: Admin): Reply =
                admin.run {
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

    class Request {
        class Create(
            val email: String,
            val name: String,
            val pw: String,
            val role: AdminRole,
            val description: String? = null,
            val parentAdminId: Long? = null,
        )

        class PasswordChange(
            val pw: String,
        )
    }

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
            fun from(admin: Admin): AdminRow =
                AdminRow(
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

package io.mustelidae.otter.lutrogale.api.domain.user.api

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

class UserManagementResources {
    class Request {
        @Schema(name = "Lutrogale.UserManagement.Request.Expire")
        data class Expire(
            @field:NotBlank @field:Email val email: String,
        )

        @Schema(name = "Lutrogale.UserManagement.Request.BulkExpire")
        data class BulkExpire(
            @field:NotEmpty val emails: List<String>,
        )
    }
}

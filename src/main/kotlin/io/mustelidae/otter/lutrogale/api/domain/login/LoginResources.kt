package io.mustelidae.otter.lutrogale.api.domain.login

import io.swagger.v3.oas.annotations.media.Schema

class LoginResources {
    class Request {
        @Schema(name = "Lutrogale.Login.Request.Create")
        data class Create(
            val email: String,
            val password: String,
        )
    }
}

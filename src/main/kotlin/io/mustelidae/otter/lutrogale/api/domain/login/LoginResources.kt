package io.mustelidae.otter.lutrogale.api.domain.login

import io.swagger.v3.oas.annotations.media.Schema

class LoginResources {

    @Schema(name = "Login.Request")
    class Request(
        val email: String,
        val password: String
    )
}

package io.mustelidae.otter.lutrogale.api.domain.login

class LoginResources {
    class Request {
        data class Create(
            val email: String,
            val password: String,
        )
    }
}

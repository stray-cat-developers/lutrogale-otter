package io.mustelidae.otter.lutrogale.api.domain.authorization.api

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import org.springframework.web.bind.annotation.RequestMethod

class AccessResources {

    @Schema(name = "Lutrogale.Access.CheckWay")
    enum class CheckWay {
        ID, URI
    }

    class Request {
        @Schema(name = "Lutrogale.Access.Request.UriBase")
        class UriBase(
            @Email
            val email: String,
            val uris: List<AccessUri>,
        )

        @Schema(name = "Lutrogale.Access.Request.IdBase")
        class IdBase(
            @Email
            val email: String,
            val ids: List<Long>,
        )
    }

    class Reply {
        @Schema(name = "Lutrogale.Access.Reply.AccessState")
        class AccessState(
            val target: String,
            val hasPermission: Boolean = false,
            val checkWay: CheckWay,
            val cause: String? = null,
        ) {

            companion object {
                fun ofAccept(targetMenuNavigationId: Long): AccessState {
                    return AccessState(
                        targetMenuNavigationId.toString(),
                        true,
                        CheckWay.ID,
                    )
                }

                fun ofDenied(targetMenuNavigationId: Long, cause: String?): AccessState {
                    return AccessState(
                        targetMenuNavigationId.toString(),
                        false,
                        CheckWay.ID,
                        cause,
                    )
                }

                fun ofAccept(targetUrl: String): AccessState {
                    return AccessState(
                        targetUrl,
                        true,
                        CheckWay.URI,
                    )
                }

                fun ofDenied(targetUrl: String, cause: String?): AccessState {
                    return AccessState(
                        targetUrl,
                        false,
                        CheckWay.URI,
                        cause,
                    )
                }
            }
        }
    }

    @Schema(name = "Lutrogale.Access.AccessUri")
    class AccessUri(
        val uri: String,
        val methodType: RequestMethod,
    ) {
        companion object {
            fun of(uri: String, requestMethod: RequestMethod): AccessUri {
                return AccessUri(uri, requestMethod)
            }
        }
    }
}

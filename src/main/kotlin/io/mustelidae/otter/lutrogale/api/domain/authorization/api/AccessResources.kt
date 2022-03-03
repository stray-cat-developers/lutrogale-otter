package io.mustelidae.otter.lutrogale.api.domain.authorization.api

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.bind.annotation.RequestMethod

class AccessResources {

    enum class CheckWay {
        ID, URI
    }

    class Request {
        @Schema(name = "Access.Request.UriBase")
        class UriBase(
            val email: String,
            val uris: List<AccessUri>
        )
        @Schema(name = "Access.Request.IdBase")
        class IdBase(
            val email: String,
            val ids: List<Long>
        )
    }

    class Reply {
        @Schema(name = "Access.Reply.AccessState")
        class AccessState(
            val target: String,
            val hasPermission: Boolean = false,
            val checkWay: CheckWay,
            val cause: String? = null
        ) {

            companion object {
                fun ofAccept(targetMenuNavigationId: Long): AccessState {
                    return AccessState(
                        targetMenuNavigationId.toString(),
                        true,
                        CheckWay.ID
                    )
                }

                fun ofDenied(targetMenuNavigationId: Long, cause: String?): AccessState {
                    return AccessState(
                        targetMenuNavigationId.toString(),
                        false,
                        CheckWay.ID,
                        cause
                    )
                }

                fun ofAccept(targetUrl: String): AccessState {
                    return AccessState(
                        targetUrl,
                        true,
                        CheckWay.URI
                    )
                }

                fun ofDenied(targetUrl: String, cause: String?): AccessState {
                    return AccessState(
                        targetUrl,
                        false,
                        CheckWay.URI,
                        cause
                    )
                }
            }
        }
    }

    @Schema(name = "Access.AccessUri")
    class AccessUri(
        val uri: String,
        val methodType: RequestMethod
    ) {
        companion object {
            fun of(uri: String, requestMethod: RequestMethod): AccessUri {
                return AccessUri(uri, requestMethod)
            }
        }
    }
}

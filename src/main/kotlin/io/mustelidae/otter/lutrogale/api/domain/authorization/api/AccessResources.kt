package io.mustelidae.otter.lutrogale.api.domain.authorization.api

import io.swagger.v3.oas.annotations.media.Schema

class AccessResources {

    class Request {

    }

    class Reply {
        @Schema(name = "Access.Reply.State")
        class AccessState(
            val target: String,
            val accessResult: Boolean = false,
            val checkWay: String,
            val cause: String? = null
        ) {

            companion object {
                fun ofAccept(targetMenuNavigationId: Long): AccessState {
                    return AccessState(
                        targetMenuNavigationId.toString(),
                        true,
                        "ID"
                    )
                }

                fun ofDenied(targetMenuNavigationId: Long, cause: String?): AccessState {
                    return AccessState(
                        targetMenuNavigationId.toString(),
                        false,
                        "ID",
                        cause
                    )
                }

                fun ofAccept(targetUrl: String): AccessState {
                    return AccessState(
                        targetUrl,
                        true,
                        "URI"
                    )
                }

                fun ofDenied(targetUrl: String, cause: String?): AccessState {
                    return AccessState(
                        targetUrl,
                        false,
                        "URI",
                        cause
                    )
                }
            }
        }
    }
}
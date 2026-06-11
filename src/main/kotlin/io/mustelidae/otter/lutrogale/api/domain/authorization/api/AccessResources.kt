package io.mustelidae.otter.lutrogale.api.domain.authorization.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.validation.constraints.Email
import org.springframework.web.bind.annotation.RequestMethod

class AccessResources {
    enum class CheckWay {
        ID,
        URI,
    }

    class Request {
        class UriBase(
            @Email
            val email: String,
            val uris: List<AccessUri>,
        )

        class IdBase(
            @Email
            val email: String,
            val ids: List<Long>,
        )

        class GraphQLBase(
            @Email
            val email: String,
            val graphQLs: List<AccessGraphQL>,
        )
    }

    class Reply {
        @JsonIgnoreProperties(ignoreUnknown = true)
        class AccessState(
            val target: String,
            val hasPermission: Boolean = false,
            val checkWay: CheckWay,
            val cause: String? = null,
        ) {
            companion object {
                fun ofAccept(targetMenuNavigationId: Long): AccessState =
                    AccessState(
                        targetMenuNavigationId.toString(),
                        true,
                        CheckWay.ID,
                    )

                fun ofDenied(
                    targetMenuNavigationId: Long,
                    cause: String?,
                ): AccessState =
                    AccessState(
                        targetMenuNavigationId.toString(),
                        false,
                        CheckWay.ID,
                        cause,
                    )

                fun ofAccept(targetUrl: String): AccessState =
                    AccessState(
                        targetUrl,
                        true,
                        CheckWay.URI,
                    )

                fun ofDenied(
                    targetUrl: String,
                    cause: String?,
                ): AccessState =
                    AccessState(
                        targetUrl,
                        false,
                        CheckWay.URI,
                        cause,
                    )
            }
        }
    }

    class AccessUri(
        val uri: String,
        val methodType: RequestMethod,
    ) {
        companion object {
            fun of(
                uri: String,
                requestMethod: RequestMethod,
            ): AccessUri = AccessUri(uri, requestMethod)
        }
    }

    class AccessGraphQL(
        val operation: String,
        val methodType: RequestMethod,
    )
}

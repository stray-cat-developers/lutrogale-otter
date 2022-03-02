package io.mustelidae.otter.lutrogale.api.domain.authorization.api

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.mustelidae.otter.lutrogale.web.commons.utils.DecryptUtil
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.bind.annotation.RequestMethod

class AccessResources {

    class Request {
        @Schema(name = "Access.Request.UriBase")
        class UriBase(
            val accessUris: List<AccessUri>
        ) {
            @Schema(name = "Access.Request.UriBase.AccessUri")
            class AccessUri(
                @JsonProperty("uri")
                val encryptedUri: String,
                val methodType: String
            ) {
                val requestMethod: RequestMethod
                    get() = RequestMethod.valueOf(methodType)

                @JsonIgnore
                fun getDecryptedUri(apiKey: String): String {
                    val key = apiKey.substring(0, 16)
                    return DecryptUtil.aes128(key, encryptedUri)
                }
            }
        }
    }

    class Reply {
        @Schema(name = "Access.Reply.AccessState")
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

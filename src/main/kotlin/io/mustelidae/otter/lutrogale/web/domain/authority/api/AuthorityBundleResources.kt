package io.mustelidae.otter.lutrogale.web.domain.authority.api

import com.fasterxml.jackson.annotation.JsonProperty
import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityDefinition
import io.swagger.v3.oas.annotations.media.Schema

class AuthorityBundleResources {

    class Request {

        @Schema(name = "Lutrogale.AuthorityBundle.Request.AuthorityBundle")
        data class AuthorityBundle(
            val groupName: String,
            val naviId: List<Long>,
        )
    }

    class Modify {
        @Schema(name = "Lutrogale.AuthorityBundle.Modify.Tree")
        data class Tree(
            val naviId: Long,
        )
    }

    class Reply {
        @Schema(name = "Lutrogale.AuthorityBundle.Reply.AuthorityBundle")
        data class AuthorityBundle(
            @Schema(name = "authId")
            @JsonProperty("authId")
            val id: Long,
            val name: String,
            val projectId: Long,
        ) {
            companion object {
                fun from(authorityDefinition: AuthorityDefinition): AuthorityBundle {
                    return authorityDefinition.run {
                        AuthorityBundle(
                            authorityDefinition.id!!,
                            name,
                            authorityDefinition.project!!.id!!,
                        )
                    }
                }
            }
        }
    }
}

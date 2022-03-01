package io.mustelidae.otter.lutrogale.web.domain.authority.api

import com.fasterxml.jackson.annotation.JsonProperty
import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityDefinition

class AuthorityBundleResources {

    class Request {

        data class AuthorityBundle(
            val groupName: String,
            val naviId: List<Long>
        )
    }

    class Modify {
        data class Tree(
            val naviId: Long
        )
    }

    class Reply {
        data class AuthorityBundle(
            @JsonProperty("authId")
            val id: Long,
            val name: String,
            val projectId: Long
        ) {
            companion object {
                fun from(authorityDefinition: AuthorityDefinition): AuthorityBundle {
                    return authorityDefinition.run {
                        AuthorityBundle(
                            authorityDefinition.id!!,
                            name,
                            authorityDefinition.project!!.id!!
                        )
                    }
                }
            }
        }
    }
}
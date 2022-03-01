package io.mustelidae.otter.lutrogale.web.domain.grant.api

import io.mustelidae.otter.lutrogale.utils.toDateString
import io.mustelidae.otter.lutrogale.web.domain.grant.AuthorityDefinition

class AuthorityDefinitionResource(
    val id: Long,
    val name: String,
    val regDate: String
) {

    companion object {
        fun of(authorityDefinition: AuthorityDefinition): AuthorityDefinitionResource {
            return authorityDefinition.run {
                AuthorityDefinitionResource(
                    authorityDefinition.id!!,
                    authorityDefinition.name,
                    authorityDefinition.createdAt!!.toDateString()
                )
            }
        }
    }
}

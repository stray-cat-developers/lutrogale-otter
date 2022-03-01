package io.mustelidae.otter.lutrogale.web.domain.grant.api

import io.mustelidae.otter.lutrogale.utils.toDateString
import io.mustelidae.otter.lutrogale.web.domain.grant.AuthorityDefinition
import java.time.LocalDateTime

/**
 * Created by seooseok on 2016. 10. 10..
 */

class AuthorityGrantResource(
    val id: Long,
    val name: String,
    val regDate: String,
    val projectId: Long,
    val projectName: String
) {

    companion object {
        fun of(authorityDefinition: AuthorityDefinition, regDate: LocalDateTime): AuthorityGrantResource {
            return authorityDefinition.run {
                AuthorityGrantResource(
                    id!!,
                    name,
                    regDate.toDateString(),
                    project!!.id!!,
                    project!!.name
                )
            }
        }

        fun of(authorityDefinition: AuthorityDefinition): AuthorityGrantResource {
            return authorityDefinition.run {
                AuthorityGrantResource(
                    id!!,
                    name,
                    createdAt!!.toDateString(),
                    project!!.id!!,
                    project!!.name
                )
            }
        }
    }
}

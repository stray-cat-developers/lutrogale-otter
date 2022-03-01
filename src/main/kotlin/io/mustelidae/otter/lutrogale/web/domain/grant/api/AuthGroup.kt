package io.mustelidae.otter.lutrogale.web.domain.grant.api

import com.fasterxml.jackson.annotation.JsonProperty
import io.mustelidae.smoothcoatedotter.web.domain.grant.AuthorityDefinition

/**
 * Created by HanJaehyun on 2016. 10. 11..
 */
class AuthGroup(
    val name: String,
    val projectId: Long,
    @JsonProperty("authId")
    val id: Long
) {
    companion object {

        fun of(authorityDefinition: AuthorityDefinition): AuthGroup {
            return authorityDefinition.run {
                AuthGroup(
                    name, authorityDefinition.project!!.id!!,
                    authorityDefinition.id!!
                )
            }
        }
    }
}

package io.mustelidae.otter.lutrogale.api.domain.authorization.api

import io.mustelidae.smoothcoatedotter.web.commons.constant.OsoriConstant
import io.mustelidae.smoothcoatedotter.api.domain.authorization.AccessUri

class AuthenticationCheckResource(
    val email: String,
    val apiKey: String,
    val authenticationCheckType: OsoriConstant.AuthenticationCheckType,
    val menuNavigationIdGroup: List<Long>? = null,
    val accessUriGroup: List<AccessUri>? = null,
) {

    companion object {
        fun ofIdBase(email: String, apiKey: String, menuNavigationIdGroup: List<Long>): AuthenticationCheckResource {

            return AuthenticationCheckResource(
                email, apiKey, OsoriConstant.AuthenticationCheckType.id, menuNavigationIdGroup
            )
        }

        fun ofUrlBase(email: String, apiKey: String, accessUriGroup: List<AccessUri>): AuthenticationCheckResource {
            return AuthenticationCheckResource(
                email, apiKey, OsoriConstant.AuthenticationCheckType.uri, accessUriGroup = accessUriGroup
            )
        }
    }

    fun getUris(): List<String> {
        require(!(authenticationCheckType !== OsoriConstant.AuthenticationCheckType.uri)) { "authentication check type is wrong" }
        return this.accessUriGroup!!.map { it.uri }.toList()
    }
}

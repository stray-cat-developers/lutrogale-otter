package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources.AccessGraphQL
import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources.AccessUri
import io.mustelidae.otter.lutrogale.common.Constant

class AccessGrant(
    val email: String,
    val apiKey: String,
    val authenticationCheckType: Constant.AuthenticationCheckType,
    val menuNavigationIdGroup: List<Long>? = null,
    val accessUriGroup: List<AccessUri>? = null,
) {
    companion object {
        fun ofIdBase(
            email: String,
            apiKey: String,
            menuNavigationIdGroup: List<Long>,
        ): AccessGrant =
            AccessGrant(
                email,
                apiKey,
                Constant.AuthenticationCheckType.ID,
                menuNavigationIdGroup,
            )

        fun ofUrlBase(
            email: String,
            apiKey: String,
            accessUriGroup: List<AccessUri>,
        ): AccessGrant =
            AccessGrant(
                email,
                apiKey,
                Constant.AuthenticationCheckType.URI,
                accessUriGroup = accessUriGroup,
            )

        fun ofOperationBase(
            email: String,
            apiKey: String,
            accessGraphQL: List<AccessGraphQL>,
        ): AccessGrant =
            AccessGrant(
                email,
                apiKey,
                Constant.AuthenticationCheckType.URI,
                accessUriGroup = accessGraphQL.map { AccessUri.of("/${it.operation}", it.methodType) },
            )
    }
}

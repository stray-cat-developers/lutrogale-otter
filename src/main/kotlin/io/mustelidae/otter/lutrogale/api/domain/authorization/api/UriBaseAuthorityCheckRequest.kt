package io.mustelidae.otter.lutrogale.api.domain.authorization.api

/**
 * Created by seooseok on 2016. 10. 27..
 */

class UriBaseAuthorityCheckRequest(
    val apiKey: String,
    val accessUriRequests: List<AccessUriRequest>
)

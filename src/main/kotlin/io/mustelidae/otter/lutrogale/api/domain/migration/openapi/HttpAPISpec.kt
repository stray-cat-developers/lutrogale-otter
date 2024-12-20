package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import org.springframework.web.bind.annotation.RequestMethod

data class HttpAPISpec(
    val url: String,
    val methods: List<RequestMethod>,
    val summary: String?,
) {
    fun getUrlParts(): List<String> {
        return url.split(Regex("(?<!:)/")).filter { it.isNotEmpty() }
    }
}

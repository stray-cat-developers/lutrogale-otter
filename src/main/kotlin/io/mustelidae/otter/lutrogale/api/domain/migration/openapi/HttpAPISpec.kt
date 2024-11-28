package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import org.springframework.http.HttpMethod

data class HttpAPISpec(
    val url: String,
    val summary: String,
    val methods: List<HttpMethod>
) {

    var blocks = urlSplit()
    private fun urlSplit(): List<String> {
        return url.split(Regex("(?<!:)/"))
    }
}
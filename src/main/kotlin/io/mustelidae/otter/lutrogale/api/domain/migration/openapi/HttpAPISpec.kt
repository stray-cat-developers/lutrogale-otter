package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import org.springframework.web.bind.annotation.RequestMethod
import java.util.ArrayDeque

data class HttpAPISpec(
    val url: String,
    val summary: String,
    val methods: List<RequestMethod>
) {

    var blocksQueue = urlSplit()
    private fun urlSplit(): ArrayDeque<String> {
        val blocks = url.split(Regex("/(?<!:)"))
        return ArrayDeque(blocks)
    }

}

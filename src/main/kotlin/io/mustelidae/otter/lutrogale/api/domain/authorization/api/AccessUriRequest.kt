package io.mustelidae.otter.lutrogale.api.domain.authorization.api

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.mustelidae.otter.lutrogale.web.commons.utils.DecryptUtil
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Created by seooseok on 2016. 10. 27..
 */

class AccessUriRequest(
    @JsonProperty("uri")
    val encryptedUri: String,
    val methodType: String
) {
    val requestMethod: RequestMethod
        get() = RequestMethod.valueOf(methodType)

    @JsonIgnore
    fun getDecryptedUri(apiKey: String): String {
        val key = apiKey.substring(0, 16)
        return DecryptUtil.aes128(key, encryptedUri)
    }
}

package io.mustelidae.otter.lutrogale.api.domain.authorization

import org.springframework.web.bind.annotation.RequestMethod

/**
 * Created by seooseok on 2016. 10. 27..
 * 권한 체크 할 URL정보
 */
class AccessUri(
    val uri: String,
    val requestMethod: RequestMethod
) {
    companion object {
        fun of(uri: String, requestMethod: RequestMethod): AccessUri {
            return AccessUri(uri, requestMethod)
        }
    }
}

package io.mustelidae.otter.lutrogale.api.domain.authorization.api

/**
 * Created by seooseok on 2016. 10. 24..
 * 권한 체크 요청 리소스
 */

class AccessResource(
    val target: String,
    val accessResult: Boolean = false,
    val checkWay: String,
    val cause: String? = null
) {

    companion object {
        fun ofAccept(targetMenuNavigationId: Long): AccessResource {
            return AccessResource(
                targetMenuNavigationId.toString(),
                true,
                "ID"
            )
        }

        fun ofDenied(targetMenuNavigationId: Long, cause: String?): AccessResource {
            return AccessResource(
                targetMenuNavigationId.toString(),
                false,
                "ID",
                cause
            )
        }

        fun ofAccept(targetUrl: String): AccessResource {
            return AccessResource(
                targetUrl,
                true,
                "URI"
            )
        }

        fun ofDenied(targetUrl: String, cause: String?): AccessResource {
            return AccessResource(
                targetUrl,
                false,
                "URI",
                cause
            )
        }
    }
}

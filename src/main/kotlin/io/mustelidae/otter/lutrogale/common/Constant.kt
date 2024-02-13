package io.mustelidae.otter.lutrogale.common

object Constant {

    object Redis {
        const val USER_LOCK = "userLockTemplate"
    }

    /**
     * Navigation Type
     */
    enum class NavigationType {
        /* 카테고리 */
        CATEGORY,

        /* 메뉴 */
        MENU,

        /* 기능 */
        FUNCTION,
    }

    enum class AuthenticationCheckType {
        URI, ID
    }
}

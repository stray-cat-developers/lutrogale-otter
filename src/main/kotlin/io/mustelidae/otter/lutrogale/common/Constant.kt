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
        category, /* 메뉴 */
        menu, /* 기능 */
        function
    }

    enum class AuthenticationCheckType {
        uri, id
    }
}

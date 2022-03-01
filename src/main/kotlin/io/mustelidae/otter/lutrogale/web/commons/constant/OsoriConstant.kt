package io.mustelidae.otter.lutrogale.web.commons.constant

/**
 * Created by seooseok on 2016. 9. 8..
 */
class OsoriConstant {
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

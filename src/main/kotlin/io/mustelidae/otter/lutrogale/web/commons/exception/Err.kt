package io.mustelidae.otter.lutrogale.web.commons.exception

/**
 * Error 타입에 대한 Interface
 */
interface Err {
    fun code(): String
    fun message(): String
    fun message(vararg args: Any?): String
}

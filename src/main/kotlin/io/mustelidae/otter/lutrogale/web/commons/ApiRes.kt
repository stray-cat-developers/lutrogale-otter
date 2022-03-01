package io.mustelidae.otter.lutrogale.web.commons

import io.mustelidae.smoothcoatedotter.web.commons.exception.Err
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Created by seooseok on 2016. 6. 30..
 * 공통 API 응답 클래스
 */
class ApiRes<T> {
    val timestamp: Long
    val code: String
    val message: String
    val result: T?

    constructor(t: T) {
        timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        this.code = okCode
        message = okMessage
        result = t
    }

    private constructor(code: String, message: String) {
        timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        this.code = code
        this.message = message
        result = null
    }

    constructor(err: Err) {
        timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        this.code = err.code()
        message = err.message()
        result = null
    }

    companion object {
        private const val okCode = "0000"
        private const val okMessage = "ok"
        @JvmStatic
        fun success(): ApiRes<*> {
            return ApiRes<Any?>(okCode, okMessage)
        }

        fun fail(err: Err): ApiRes<*> {
            return ApiRes<Any?>(err)
        }

        @JvmStatic
        fun fail(code: String, message: String): ApiRes<*> {
            return ApiRes<Any?>(code, message)
        }
    }
}

fun <T> T.toApiRes(): ApiRes<T> = ApiRes(this)
fun <T> List<T>.toApiRes(): ApiRes<List<T>> = ApiRes(this)

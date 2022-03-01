package io.mustelidae.otter.lutrogale.web.commons.exception

import java.lang.RuntimeException
import java.text.MessageFormat

/**
 * 어플리케이션에서 사용 할 Exception
 */
class ApplicationException : RuntimeException {
    var code: String? = null
        private set
    var msg: String? = null
        private set
    var loggingMsg: String? = null
        private set

    /**
     * Custom Exception
     *
     * @param error 에러코드
     */
    constructor(error: Err) : super(String.format("[%s] %s", error.code(), error.message())) {
        bindParams(error, error.message())
    }

    /**
     * Custom Exception
     *
     * @param error  에러코드
     * @param errMsg 커스터마이징 할 에러 메시지
     */
    constructor(error: Err, errMsg: String?) : super(String.format("[%s] %s", error.code(), errMsg)) {
        bindParams(error, errMsg)
    }

    /**
     * Custom Exception
     *
     * @param error      에러코드
     * @param errMsg     커스터마이징 할 에러 메시지
     * @param loggingMsg 별도 로그로 남겨야 할 상세 정보들
     */
    constructor(error: Err, errMsg: String?, loggingMsg: String) : super(
        String.format(
            "[%s] %s",
            error.code(),
            errMsg
        )
    ) {
        bindParams(error, errMsg)
        this.loggingMsg = "logging msg: $loggingMsg"
    }

    /**
     * Custom Exception
     *
     * @param error   에러코드
     * @param msgArgs 에러 메시지 출력 시 같이 출력되어야 할 파라미터들 ex) new Object[]{"value1","value2"}
     */
    constructor(error: Err, msgArgs: Array<Any?>) : super(
        MessageFormat.format(
            "[{0}] {1}",
            error.code(),
            error.message(*msgArgs)
        )
    ) {
        bindParams(error, error.message(*msgArgs))
    }

    /**
     * Custom Exception
     *
     * @param error      에러코드
     * @param msgArgs    에러 메시지 출력 시 같이 출력되어야 할 파라미터들 ex) new Object[]{"value1","value2"}
     * @param loggingMsg 별도 로그로 남겨야 할 상세 정보들
     */
    constructor(error: Err, msgArgs: Array<Any?>, loggingMsg: String) : super(
        String.format(
            "[%s] %s",
            *arrayOf<Any?>(error.code(), error.message(*msgArgs))
        )
    ) {
        bindParams(error, error.message(*msgArgs))
        this.loggingMsg = "logging msg: $loggingMsg"
    }

    private fun bindParams(error: Err, errMsg: String?) {
        this.code = error.code()
        msg = errMsg
    }
}

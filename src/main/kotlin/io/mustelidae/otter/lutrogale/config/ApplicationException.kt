package io.mustelidae.otter.lutrogale.config

import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.web.common.exception.Err

/**
 * 어플리케이션에서 사용 할 Exception
 */

class ApplicationException : CustomException {
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
    constructor(error: Err) : super(DefaultError(ErrorCode.P000, error.message())) {
        bindParams(error, error.message())
    }

    /**
     * Custom Exception
     *
     * @param error  에러코드
     * @param errMsg 커스터마이징 할 에러 메시지
     */
    constructor(error: Err, errMsg: String?) : super(DefaultError(ErrorCode.P000, errMsg ?: error.message())) {
        bindParams(error, errMsg)
    }

    /**
     * Custom Exception
     *
     * @param error   에러코드
     * @param msgArgs 에러 메시지 출력 시 같이 출력되어야 할 파라미터들 ex) new Object[]{"value1","value2"}
     */
    constructor(error: Err, msgArgs: Array<Any?>) : super(DefaultError(ErrorCode.P000, msgArgs.joinToString(","))) {
        bindParams(error, error.message(*msgArgs))
    }

    private fun bindParams(error: Err, errMsg: String?) {
        this.code = error.code()
        msg = errMsg
    }
}

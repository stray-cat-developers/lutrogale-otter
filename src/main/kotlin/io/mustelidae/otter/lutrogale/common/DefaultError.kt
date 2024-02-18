package io.mustelidae.otter.lutrogale.common

class DefaultError : ErrorSource {
    override val message: String
    override var causeBy: Map<String, Any?>? = null

    constructor(errorCode: ErrorCode, message: String, causeBy: Map<String, Any?>? = null) {
        this.message = message
        this.causeBy = causeBy
        this.code = errorCode.name
    }

    constructor(errorCode: ErrorCode) {
        this.message = errorCode.summary
        this.code = errorCode.name
    }

    override val code: String
    override var refCode: String? = null
}

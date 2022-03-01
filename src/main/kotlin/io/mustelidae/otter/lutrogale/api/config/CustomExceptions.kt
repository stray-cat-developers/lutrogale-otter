package io.mustelidae.otter.lutrogale.api.config

import io.mustelidae.otter.lutrogale.api.common.Error
import io.mustelidae.otter.lutrogale.api.common.ErrorCode
import io.mustelidae.otter.lutrogale.api.common.ErrorSource

open class CustomException(val error: ErrorSource) : RuntimeException(error.message)

/**
 * Human Exception
 */
open class HumanException(error: ErrorSource) : CustomException(error)
class DataNotFindException : HumanException {
    constructor(message: String) : super(Error(ErrorCode.HD00, message))
    constructor(id: String, message: String) : super(Error(ErrorCode.HD00, message, mapOf("id" to id)))
    constructor(id: Long, message: String) : this(id.toString(), message)
}

class PreconditionFailException(message: String) : HumanException(Error(ErrorCode.HD02, message))

// ignore in sentry
class DataNotSearchedException : HumanException {
    constructor(message: String) : super(Error(ErrorCode.HD01, message))
    constructor(source: String, message: String) : super(Error(ErrorCode.HD01, message, mapOf("search source" to source)))
    constructor(id: Long, message: String) : this(id.toString(), message)
}

class MissingRequestXHeaderException(headerName: String) : HumanException(Error(ErrorCode.HI02, "Missing request header $headerName"))

class InvalidArgumentException(message: String) : HumanException(Error(ErrorCode.HI01, message))

open class SystemException(error: ErrorSource) : CustomException(error)
class DevelopMistakeException : SystemException {
    constructor(errorCode: ErrorCode) : super(Error(errorCode, errorCode.summary))
    constructor(message: String, causeBy: Map<String, Any?>? = null) : super(Error(ErrorCode.PD01, message, causeBy))
}

open class CommunicationException(error: ErrorSource) : CustomException(error)
class ClientException(target: String, message: String, code: String? = null) : CommunicationException(
    Error(
        ErrorCode.C000,
        message,
        causeBy = mapOf(
            "target" to target,
            "clientErrorCode" to code
        )
    ).apply {
        refCode = code
    }
)
class ConnectionTimeoutException(target: String, timeoutConfig: Int, url: String) : CommunicationException(
    Error(
        ErrorCode.CT01,
        "$target Connection fail",
        causeBy = mapOf(
            "target" to target,
            "url" to url,
            "timeout" to timeoutConfig
        )
    )
)

class ReadTimeoutException(target: String, timeoutConfig: Int, url: String) : CommunicationException(
    Error(
        ErrorCode.CT02,
        "$target Data not Received",
        causeBy = mapOf(
            "target" to target,
            "url" to url,
            "timeout" to timeoutConfig
        )
    )
)

open class AsyncException(message: String, causeBy: Map<String, Any?>? = null) : CustomException(
    Error(
        ErrorCode.SA00,
        message, causeBy
    )
)

/**
 * Policy Exception
 */
open class PolicyException(error: ErrorSource) : CustomException(error)

/**
 * UnAuthorized Exception
 */
open class UnAuthorizedException(error: ErrorSource) : CustomException(error)

class PermissionException : UnAuthorizedException {
    constructor() : super(Error(ErrorCode.HA00, "Access denied"))
    constructor(message: String) : super(Error(ErrorCode.HA01, message))
}

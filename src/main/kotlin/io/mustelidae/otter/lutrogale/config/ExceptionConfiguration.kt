@file:Suppress("unused")

package io.mustelidae.otter.lutrogale.config

import com.google.common.base.Strings
import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.common.ErrorSource
import io.mustelidae.otter.lutrogale.utils.Jackson
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.core.env.Environment
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.PRECONDITION_FAILED
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletWebRequest
import java.time.LocalDateTime

@ControllerAdvice(annotations = [RestController::class])
class ExceptionConfiguration(
    private val env: Environment,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * the developer does not know about.
     */
    @ExceptionHandler(value = [RuntimeException::class])
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleGlobalException(e: RuntimeException, request: HttpServletRequest): GlobalErrorFormat {
        log.error("Unexpected error", e)
        return errorForm(request, e, DefaultError(ErrorCode.S000, "Oops, something went wrong."))
    }

    @ExceptionHandler(value = [InvalidDataAccessApiUsageException::class])
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleInvalidDataAccessApiUsageException(e: InvalidDataAccessApiUsageException, request: HttpServletRequest): GlobalErrorFormat {
        return errorForm(request, e, DefaultError(ErrorCode.SD01, e.message!!))
    }

    /**
     * policy is not defined.
     */
    @ExceptionHandler(value = [IllegalStateException::class])
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleIllegalStateException(e: IllegalStateException, request: HttpServletRequest): GlobalErrorFormat {
        return errorForm(request, e, DefaultError(ErrorCode.P000, "Oops, something went wrong."))
    }

    /**
     * Development mistakes declared by developers
     */
    @ExceptionHandler(value = [DevelopMistakeException::class])
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleDevelopMistakeException(e: DevelopMistakeException, request: HttpServletRequest): GlobalErrorFormat {
        return errorForm(request, e, e.error)
    }

    /**
     * Invalid input
     */
    @ExceptionHandler(value = [IllegalArgumentException::class])
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    fun handleIllegalArgumentException(e: IllegalArgumentException, request: HttpServletRequest): GlobalErrorFormat {
        log.error("[T] wrong input.", e)
        return errorForm(request, e, DefaultError(ErrorCode.HI01, "Invalid input"))
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): GlobalErrorFormat {
        return errorForm(
            request,
            e,
            DefaultError(
                ErrorCode.HI00,
                e.bindingResult.fieldError?.defaultMessage ?: run {
                    ErrorCode.HI00.summary
                },
            ),
        )
    }

    @ExceptionHandler(value = [CommunicationException::class])
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleCommunicationException(
        e: CommunicationException,
        request: HttpServletRequest,
    ): GlobalErrorFormat {
        return errorForm(request, e, e.error)
    }

    /**
     * human error
     */
    @ExceptionHandler(value = [HumanException::class])
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    fun handleHumanException(
        e: HumanException,
        request: HttpServletRequest,
    ): GlobalErrorFormat {
        return errorForm(request, e, e.error)
    }

    /**
     * application policy
     */
    @ExceptionHandler(value = [PolicyException::class])
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    fun policyException(
        e: PolicyException,
        request: HttpServletRequest,
    ): GlobalErrorFormat {
        return errorForm(request, e, e.error)
    }

    /**
     * unauthorized
     */
    @ExceptionHandler(value = [UnAuthorizedException::class])
    @ResponseStatus(UNAUTHORIZED)
    @ResponseBody
    fun unAuthorizedException(
        e: UnAuthorizedException,
        request: HttpServletRequest,
    ): GlobalErrorFormat {
        return errorForm(request, e, e.error)
    }

    /**
     * data not found
     */
    @ExceptionHandler(value = [DataNotFindException::class])
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    fun dataNotFindException(
        e: DataNotFindException,
        request: HttpServletRequest,
    ): GlobalErrorFormat {
        return errorForm(request, e, e.error)
    }

    @ExceptionHandler(value = [PreconditionFailException::class])
    @ResponseStatus(PRECONDITION_FAILED)
    @ResponseBody
    fun preconditionFailException(
        e: PreconditionFailException,
        request: HttpServletRequest,
    ): GlobalErrorFormat {
        return errorForm(request, e, e.error)
    }

    @ExceptionHandler(ApplicationException::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleApplicationException(e: ApplicationException, request: HttpServletRequest): GlobalErrorFormat {
        if (Strings.isNullOrEmpty(e.loggingMsg)) log.error(e.loggingMsg)

        return GlobalErrorFormat(
            LocalDateTime.now().toString(),
            500,
            e.code!!,
            null,
            e.msg!!,
            e.javaClass.simpleName,
        )
    }

    private fun errorForm(request: HttpServletRequest, e: Exception, error: ErrorSource): GlobalErrorFormat {
        val errorAttributeOptions = if (env.activeProfiles.contains("prod").not()) {
            ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE)
        } else {
            ErrorAttributeOptions.defaults()
        }

        val errorAttributes =
            DefaultErrorAttributes().getErrorAttributes(ServletWebRequest(request), errorAttributeOptions)

        errorAttributes.apply {
            this["message"] = error.message
            this["code"] = error.code
            this["causeBy"] = error.causeBy
            this["type"] = e.javaClass.simpleName
        }

        return Jackson.getMapper().convertValue(errorAttributes, GlobalErrorFormat::class.java)
    }

    @Suppress(
        "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
        "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    )
    private fun methodArgumentNotValidExceptionErrorForm(errors: List<FieldError>) =
        errors.map {
            ValidationError(
                field = it.field,
                rejectedValue = it.rejectedValue.toString(),
                message = it.defaultMessage,
            )
        }.toList()

    private data class ValidationError(
        val field: String,
        val rejectedValue: String,
        val message: String?,
    )
}

package io.mustelidae.otter.lutrogale.config

import jakarta.servlet.ServletException
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.context.annotation.Configuration
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.WebRequest
import java.util.Date

/**
 * Created by seooseok on 2016. 9. 29..
 * API servlet error spec 정의
 */
@Configuration
class OsoriErrorAttributes : DefaultErrorAttributes() {
    fun getErrorAttributes(requestAttributes: RequestAttributes, includeStackTrace: Boolean): Map<String, Any?> {
        val errorAttributes: MutableMap<String, Any?> = LinkedHashMap()
        errorAttributes["timestamp"] = Date()
        addCode(errorAttributes, requestAttributes)
        addErrorDetails(errorAttributes, requestAttributes)
        return errorAttributes
    }

    private fun addCode(errorAttributes: MutableMap<String, Any?>, requestAttributes: RequestAttributes) {
        val status = getAttribute<Int>(requestAttributes, "jakarta.servlet.error.status_code")
        if (status == null) {
            errorAttributes["code"] = "S998"
            return
        }
        errorAttributes["code"] = status
    }

    private fun addErrorDetails(errorAttributes: MutableMap<String, Any?>, requestAttributes: RequestAttributes) {
        var error: Throwable? = getError(requestAttributes as WebRequest?)
        if (error != null) {
            while (error is ServletException && error.cause != null) {
                error = error.cause
            }
            errorAttributes["exception"] = error!!.javaClass.name
            addErrorMessage(errorAttributes, error)
        }
        val message = getAttribute<String>(requestAttributes, "jakarta.servlet.error.message") as String
        if ((message.isEmpty().not() || errorAttributes["message"] == null) &&
            error !is BindingResult
        ) {
            errorAttributes["message"] =
                message.ifEmpty { "No message available" }
        }
    }

    private fun addErrorMessage(errorAttributes: MutableMap<String, Any?>, error: Throwable?) {
        val result: BindingResult? = extractBindingResult(error)
        if (result == null) {
            errorAttributes["message"] = error!!.message
            return
        }
        if (result.errorCount > 0) {
            errorAttributes["message"] = (
                "Validation failed for object='" + result.objectName +
                    "'. Error count: " + result.errorCount
                )
        } else {
            errorAttributes["message"] = "No errors"
        }
    }

    private fun extractBindingResult(error: Throwable?): BindingResult? {
        if (error is MethodArgumentNotValidException) {
            return (error as MethodArgumentNotValidException?)?.bindingResult
        }

        if (error is BindingResult) {
            return error
        }
        return null
    }

    private fun <T> getAttribute(requestAttributes: RequestAttributes, name: String): Any? {
        return requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST)
    }
}

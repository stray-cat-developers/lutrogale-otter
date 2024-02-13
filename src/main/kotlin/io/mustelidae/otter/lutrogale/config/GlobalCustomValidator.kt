package io.mustelidae.otter.lutrogale.config

import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.InitBinder
import java.util.Objects

@ControllerAdvice
class WebDataBindHandler {

    @Autowired
    private lateinit var localValidatorFactoryBean: LocalValidatorFactoryBean

    @Suppress("unused")
    @InitBinder
    internal fun initBinder(binder: WebDataBinder) {
        binder.addValidators(CollectionListValidator(localValidatorFactoryBean))
    }
}

/**
 * List 안 객체를 재귀적으로 유효성 체크
 */
class CollectionListValidator(localValidatorFactoryBean: LocalValidatorFactoryBean) : Validator {

    private val validator: Validator = localValidatorFactoryBean

    override fun supports(clazz: Class<*>): Boolean {
        return clazz.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        if (target is List<*>) {
            for (resource in target) {
                resource?.let { validate(it, errors) }
            }
            return
        }

        ValidationUtils.invokeValidator(validator, target, errors)

        if (target.javaClass.isPrimitive) {
            return
        }

        validateOnProperty(target, errors)
    }

    private fun validateOnProperty(target: Any, errors: Errors) {
        val propertyDescriptors = BeanUtils.getPropertyDescriptors(target.javaClass)

        for (descriptor in propertyDescriptors) {
            if (List::class.java.isAssignableFrom(descriptor.propertyType)) {
                val method = descriptor.readMethod

                try {
                    val any = method.invoke(target)

                    if (!Objects.isNull(any)) {
                        val typeOfObject = any as List<*>

                        for (resource in typeOfObject) {
                            resource?.let { validate(it, errors) }
                        }
                    }
                } catch (e: ReflectiveOperationException) {
                    val error = FieldError(target.javaClass.simpleName, descriptor.name, e.localizedMessage)
                    errors.fieldErrors.add(error)
                }
            }
        }
    }
}

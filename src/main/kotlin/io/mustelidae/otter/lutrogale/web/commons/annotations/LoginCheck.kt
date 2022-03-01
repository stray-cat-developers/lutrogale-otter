package io.mustelidae.otter.lutrogale.web.commons.annotations

/**
 * Created by seooseok on 2016. 9. 8..
 * 로그인 체크 어노테이션
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginCheck(val enable: Boolean = true)

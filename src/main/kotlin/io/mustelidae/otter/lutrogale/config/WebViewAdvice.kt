package io.mustelidae.otter.lutrogale.config

import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice(basePackages = ["io.mustelidae.otter.lutrogale.web"])
class WebViewAdvice(
    private val buildProperties: BuildProperties,
) {
    @ModelAttribute("appVersion")
    fun appVersion(): String = buildProperties.version
}

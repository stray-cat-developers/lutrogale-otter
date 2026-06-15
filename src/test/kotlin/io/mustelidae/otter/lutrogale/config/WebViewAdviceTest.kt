package io.mustelidae.otter.lutrogale.config

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.info.BuildProperties
import java.util.Properties

class WebViewAdviceTest :
    StringSpec({
        "BuildProperties의 version을 appVersion 모델 속성으로 반환한다" {
            val props = Properties().apply { setProperty("version", "1.1.0") }
            val advice = WebViewAdvice(BuildProperties(props))

            advice.appVersion() shouldBe "1.1.0"
        }
    })

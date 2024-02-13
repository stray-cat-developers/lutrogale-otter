package io.mustelidae.otter.lutrogale.web.commons.exception

import java.text.MessageFormat

/**
 * Created by seooseok on 2016. 7. 12..
 * 어플리케이션의 비즈니스 제어 에러
 */
enum class ProcessErr(private val code: String, private val message: String) : Err {
    FAIL_IMAGE_CONTROL("P001", "이미지 처리를 실패하였습니다. {0}"), FAIL_USER_AUTH(
        "P002",
        "사용자 계정 처리에 실패하였습니다. {0}",
    ),
    ALREADY_EXPIRED("P003", "이미 만료되었습니다. {0}"), WRONG_DEVELOP_PROCESS("P004", "해당 요청이 잘못된 작업을 수행하고 있습니다.");

    override fun code(): String {
        return code
    }

    override fun message(): String {
        return message.replace("\\{.*\\}".toRegex(), "")
    }

    override fun message(vararg args: Any?): String {
        return MessageFormat.format(message, *args).replace("\\{.*\\}".toRegex(), "")
    }
}

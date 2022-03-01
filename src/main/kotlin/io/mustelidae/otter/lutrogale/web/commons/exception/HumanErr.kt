package io.mustelidae.otter.lutrogale.web.commons.exception

import java.text.MessageFormat

/**
 * 사용자 에러
 * 유저가 내는 에러로 유저는 사람만을 의미하지 않으면 API 시스템을 사용하는 모두가 해당된다. API call 자체를 잘못 날리는 경우 해당 에러코드를 이용한다.
 */
enum class HumanErr(private val code: String, private val message: String) : Err {
    // FIXME : 에러 메시지 나중에 재정의 하자.
    INVALID_ARGS("H001", "입력값이 잘못되었습니다. {0}"), FAIL_BIND("H002", "URL {0} 파라미터 바인딩에 실패하였습니다. {1} 문서를 참조하세요."), IS_EMPTY(
        "H003",
        "{0} 존재하지 않는 데이터입니다."
    ),
    IS_EXIST("H004", "{0} 이미 해당 데이터가 있습니다."), INVALID_ACCESS("H005", " 접근 할 수 없습니다. {0}"), IS_EXPIRE(
        "H006",
        "해당 데이터가 만료되었습니다."
    ),
    INVALID_INCLUDE("H007", "{0}은 {1}의 데이터가 아닙니다"), FAIL_LOGIN("H008", "Email과 Password가 다릅니다."), INVALID_USER(
        "H009",
        "{0} 유저는 권한 시스템에 등록된 유저가 아닙니다. 권한 설정을 해주세요."
    ),
    MISSING_PARAM("H010", "{0} 파라미터 값이 없습니다."), INVALID_APIKEY("H011", "유효하지 않은 APIKEY 입니다."), INVALID_URL(
        "H012",
        "유효하지 않은 URL형식 입니다"
    );

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

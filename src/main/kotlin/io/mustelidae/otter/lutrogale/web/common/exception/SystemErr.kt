package io.mustelidae.otter.lutrogale.web.common.exception

import com.google.common.base.Joiner

/**
 * Created by seooseok on 2016. 7. 6..
 * 어플리케이션 자체 시스템 에러 및 연관 시스템 간 통신 에러
 */
enum class SystemErr(private val code: String, private val message: String) : Err {
    ERROR_UNKNOWN(
        "S999",
        "알 수 없는 오류가 발생하였습니다. 업주/업소 개발팀에 문의해주세요. 업무 JIRA: http://jira.woowa.in/secure/RapidBoard.jspa?rapidView=82&projectKey=TWOUPMNG ",
    ),
    ERROR_ENCRYPT("S001", "암호화에 실패하였습니다. "),
    ;

    override fun code(): String {
        return code
    }

    override fun message(): String {
        return message
    }

    override fun message(vararg args: Any?): String {
        return message + " (" + Joiner.on(",").join(args) + ")"
    }
}

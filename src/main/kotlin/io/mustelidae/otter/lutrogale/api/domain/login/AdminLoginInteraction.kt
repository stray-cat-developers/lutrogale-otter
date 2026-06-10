package io.mustelidae.otter.lutrogale.api.domain.login

import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.HumanException
import io.mustelidae.otter.lutrogale.web.domain.admin.Admin
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminFinder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by seooseok on 2016. 10. 18..
 */
@Service
@Transactional(readOnly = true)
class AdminLoginInteraction(
    private val adminFinder: AdminFinder,
) {
    fun loginCheck(
        email: String,
        pw: String,
    ): Admin {
        val loginFailedError = DefaultError(ErrorCode.HA02, "로그인이 실패했습니다. 이메일과 비밀번호를 다시 확인해 주세요.")
        val admin =
            adminFinder.findByEmailOrNull(email)
                ?: throw HumanException(loginFailedError)
        if (!admin.matchesPassword(pw)) throw HumanException(loginFailedError)
        if (!admin.status) throw HumanException(loginFailedError)
        return admin
    }
}

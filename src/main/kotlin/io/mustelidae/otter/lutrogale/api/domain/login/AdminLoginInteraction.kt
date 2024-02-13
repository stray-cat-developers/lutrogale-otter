package io.mustelidae.otter.lutrogale.api.domain.login

import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.HumanException
import io.mustelidae.otter.lutrogale.config.PolicyException
import io.mustelidae.otter.lutrogale.utils.Crypto
import io.mustelidae.otter.lutrogale.web.domain.admin.Admin
import io.mustelidae.otter.lutrogale.web.domain.admin.repository.AdminRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by seooseok on 2016. 10. 18..
 */
@Service
@Transactional(readOnly = true)
class AdminLoginInteraction(
    private val adminRepository: AdminRepository,
) {
    fun loginCheck(email: String, pw: String): Admin {
        val encryptedPw = Crypto.sha256(pw)
        val admin = adminRepository.findByEmailAndPw(email, encryptedPw)
            ?: throw HumanException(DefaultError(ErrorCode.HA02, "로그인이 실패했습니다. 이메일과 비밀번호를 다시 확인해 주세요."))
        if (!admin.status) throw PolicyException(DefaultError(ErrorCode.PL02, "해당 사용자는 로그인 권한이 만료되었습니다."))
        return admin
    }
}

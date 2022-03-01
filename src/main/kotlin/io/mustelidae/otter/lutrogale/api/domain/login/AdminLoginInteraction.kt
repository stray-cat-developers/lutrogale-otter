package io.mustelidae.otter.lutrogale.api.domain.login

import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.HumanErr
import io.mustelidae.otter.lutrogale.web.commons.utils.EncryptUtil.sha256
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
    private val adminRepository: AdminRepository
) {
    fun loginCheck(email: String, pw: String): Admin {
        val encryptedPw = sha256(pw)
        val admin = adminRepository.findByEmailAndPw(email, encryptedPw)
            ?: throw ApplicationException(HumanErr.FAIL_LOGIN)
        if (!admin.status) throw ApplicationException(HumanErr.IS_EXPIRE, "해당 유저는 로그인 권한이 만료되었습니다.")
        return admin
    }
}

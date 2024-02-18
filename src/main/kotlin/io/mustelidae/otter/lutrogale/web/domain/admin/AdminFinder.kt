package io.mustelidae.otter.lutrogale.web.domain.admin

import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.DataNotFindException
import io.mustelidae.otter.lutrogale.config.PolicyException
import io.mustelidae.otter.lutrogale.web.domain.admin.repository.AdminRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminFinder(
    private val adminRepository: AdminRepository,
) {

    fun findBy(id: Long): Admin {
        val admin = adminRepository.findByIdOrNull(id) ?: throw DataNotFindException("어드민 정보가 존재하지 않습니다.")
        if (!admin.status) {
            throw throw PolicyException(DefaultError(ErrorCode.PL02, "해당 사용자는 로그인 권한이 만료되었습니다."))
        }

        return admin
    }
}

package io.mustelidae.otter.lutrogale.web.domain.admin

import io.mustelidae.smoothcoatedotter.web.commons.exception.ApplicationException
import io.mustelidae.smoothcoatedotter.web.commons.exception.HumanErr
import io.mustelidae.smoothcoatedotter.web.domain.admin.repository.AdminRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminFinder(
    private val adminRepository: AdminRepository
) {

    fun findBy(id: Long): Admin {
        val admin =  adminRepository.findByIdOrNull(id) ?: throw ApplicationException(HumanErr.IS_EMPTY)
        if (!admin.status)
            throw ApplicationException(HumanErr.IS_EXPIRE)

        return admin
    }
}
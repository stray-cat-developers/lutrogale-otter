package io.mustelidae.otter.lutrogale.web.domain.admin

import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.PermissionException
import io.mustelidae.otter.lutrogale.config.PolicyException
import io.mustelidae.otter.lutrogale.web.domain.admin.repository.AdminRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by seooseok on 2016. 10. 12..
 */
@Service
@Transactional
class AdminInteraction(
    private val adminRepository: AdminRepository,
    private val adminFinder: AdminFinder,
) {
    fun register(email: String, pw: String, name: String, description: String?, img: String?): Admin {
        val admin = Admin.of(email, pw, name, description, img)
        return adminRepository.save(admin)
    }

    fun modifyBy(adminId: Long, imageUrl: String, description: String, pw: String?) {
        val admin = adminFinder.findBy(adminId)

        admin.apply {
            this.img = imageUrl
            this.description = description
        }

        if (pw.isNullOrEmpty().not()) {
            admin.setPassword(pw!!)
        }

        adminRepository.save(admin)
    }

    fun registerBy(email: String, pw: String, name: String, description: String?, img: String?, role: AdminRole, parentAdminId: Long?): Long {
        val admin = Admin.of(email, pw, name, description, img, role)
        if (parentAdminId != null) {
            val parentAdmin = adminFinder.findBy(parentAdminId)
            admin.setBy(parentAdmin)
        }
        return adminRepository.save(admin).id!!
    }

    fun expireBy(adminId: Long, requestingAdminId: Long) {
        if (adminId == requestingAdminId) {
            throw PolicyException(DefaultError(ErrorCode.PL02, "본인 계정은 만료할 수 없습니다."))
        }
        val admin = adminFinder.findBy(adminId)
        admin.expire()
        adminRepository.save(admin)
    }

    fun changePasswordBy(targetAdminId: Long, requestingAdminId: Long, callerRole: AdminRole, newPw: String) {
        if (callerRole == AdminRole.REGULAR && targetAdminId != requestingAdminId) {
            throw PermissionException("본인 계정의 비밀번호만 변경할 수 있습니다.")
        }
        val admin = adminFinder.findBy(targetAdminId)
        admin.setPassword(newPw)
        adminRepository.save(admin)
    }
}

package io.mustelidae.otter.lutrogale.web.domain.admin

import com.google.common.base.Strings
import io.mustelidae.smoothcoatedotter.web.commons.exception.ApplicationException
import io.mustelidae.smoothcoatedotter.web.commons.exception.HumanErr
import io.mustelidae.smoothcoatedotter.web.domain.admin.repository.AdminRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by seooseok on 2016. 10. 12..
 */
@Service
@Transactional
class AdminManager(
    private val adminRepository: AdminRepository,
    private val adminFinder: AdminFinder
) {
    fun register(email: String, pw: String, name: String, description: String?, img: String?): Admin {
        val admin = Admin.of(
            email, pw, name, description, img
        )
        return adminRepository.save(admin)
    }

    fun modifyBy(adminId: Long, imageUrl: String, description: String, pw: String?) {
        val admin = adminFinder.findBy(adminId)

        admin.apply {
            this.img = imageUrl
            this.description = description
        }

        if(pw.isNullOrEmpty().not())
            admin.setPassword(pw!!)

        adminRepository.save(admin)
    }


}

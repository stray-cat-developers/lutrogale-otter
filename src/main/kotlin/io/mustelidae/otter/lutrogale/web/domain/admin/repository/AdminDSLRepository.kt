package io.mustelidae.otter.lutrogale.web.domain.admin.repository

import io.mustelidae.otter.lutrogale.web.domain.admin.Admin
import io.mustelidae.otter.lutrogale.web.domain.admin.QAdmin.admin
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class AdminDSLRepository : QuerydslRepositorySupport(Admin::class.java) {

    fun findAllActive(): List<Admin> {
        return from(admin)
            .leftJoin(admin.admins).fetchJoin()
            .leftJoin(admin.parentAdmin).fetchJoin()
            .where(admin.status.isTrue)
            .fetch()
            .distinct()
    }
}

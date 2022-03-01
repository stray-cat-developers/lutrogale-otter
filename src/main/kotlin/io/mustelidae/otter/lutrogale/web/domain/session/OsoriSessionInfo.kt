package io.mustelidae.otter.lutrogale.web.domain.session

import io.mustelidae.otter.lutrogale.web.domain.admin.Admin

/**
 * Created by seooseok on 2016. 10. 12..
 */
class OsoriSessionInfo(
    val adminId: Long,
    val adminEmail: String,
    val adminName: String? = null
) {

    companion object {
        fun of(admin: Admin): OsoriSessionInfo {
            return OsoriSessionInfo(
                admin.id!!,
                admin.email,
                admin.name
            )
        }
    }
}

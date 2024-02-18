package io.mustelidae.otter.lutrogale.web

import io.mustelidae.otter.lutrogale.web.domain.session.SessionInfo
import jakarta.servlet.http.HttpSession

class AdminSession(
    private val httpSession: HttpSession,
) {
    private var adminId: Any? = httpSession.getAttribute("adminId")
    private var adminEmail: Any? = httpSession.getAttribute("adminEmail")
    private var adminName: Any? = httpSession.getAttribute("adminName")

    fun hasSession(): Boolean = (adminId != null)

    fun infoOrThrow(): SessionInfo {
        require(!(adminId == null || adminEmail == null || adminName == null)) { "admin login session is null" }
        return SessionInfo(
            adminId as Long,
            adminEmail as String,
            adminName as String,
        )
    }

    fun add(sessionInfo: SessionInfo) {
        httpSession.setAttribute("adminId", sessionInfo.adminId)
        adminId = sessionInfo.adminId
        httpSession.setAttribute("adminEmail", sessionInfo.adminEmail)
        adminEmail = sessionInfo.adminEmail
        httpSession.setAttribute("adminName", sessionInfo.adminName)
        adminName = sessionInfo.adminName
    }

    fun getAdminId(): Long? {
        return if (adminId != null) {
            adminId as Long
        } else {
            null
        }
    }
}

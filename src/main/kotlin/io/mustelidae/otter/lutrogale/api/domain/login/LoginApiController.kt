package io.mustelidae.otter.lutrogale.api.domain.login

import io.mustelidae.smoothcoatedotter.web.commons.ApiRes
import io.mustelidae.smoothcoatedotter.web.commons.utils.RequestHelper.addSessionBy
import io.mustelidae.smoothcoatedotter.web.domain.admin.Admin
import io.mustelidae.smoothcoatedotter.web.domain.session.OsoriSessionInfo
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

/**
 * Created by htwoh on 2017. 3. 2..
 */
@RestController
class LoginApiController(
    private val httpSession: HttpSession,
    private val adminLogin: AdminLogin
) {

    @PostMapping("/v1/check-login")
    fun checkLogin(@RequestBody request: LoginResources.Request, httpServletRequest: HttpServletRequest): ApiRes<*> {


        val admin: Admin = adminLogin.loginCheck("admin@osori.com", "admin")
        val sessionInfo = OsoriSessionInfo.of(admin)
        addSessionBy(httpSession, sessionInfo)
        return ApiRes<Any?>(admin.email)
    }
}

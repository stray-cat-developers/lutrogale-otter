package io.mustelidae.otter.lutrogale.web.domain.admin.api

import io.mustelidae.smoothcoatedotter.web.commons.ApiRes
import io.mustelidae.smoothcoatedotter.web.commons.ApiRes.Companion.success
import io.mustelidae.smoothcoatedotter.web.commons.utils.RequestHelper.getSessionByAdmin
import io.mustelidae.smoothcoatedotter.web.domain.admin.AdminFinder
import io.mustelidae.smoothcoatedotter.web.domain.admin.AdminManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

/**
 * Created by seooseok on 2016. 10. 18..
 */
@RestController
@RequestMapping("/management/admin")
class AdminController(
    private val adminManager: AdminManager,
    private val adminFinder: AdminFinder,
    private val httpSession: HttpSession
) {
    @GetMapping
    fun findOne(): ApiRes<*> {
        val sessionInfo = getSessionByAdmin(httpSession)
        val admin = adminFinder.findBy(sessionInfo.adminId)
        val adminResource = AdminResource.of(admin)
        return ApiRes<Any?>(adminResource)
    }

    // NOTI: admin의 경우 별도 ID를 받지 않는데 이는 profile 수정의 대상은 무조건 로그인 된 대상만 처리 할 수 있도록 하기 위함이다.
    @PutMapping
    fun modifyInfo(
        @RequestBody modify: AdminResources.Modify
    ): ApiRes<*> {
        val sessionInfo = getSessionByAdmin(httpSession)
        adminManager.modifyBy(sessionInfo.adminId, modify.imageUrl, modify.description, modify.pw!!)
        return success()
    }
}
package io.mustelidae.otter.lutrogale.web.domain.admin.api

import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.commons.ApiRes.Companion.success
import io.mustelidae.otter.lutrogale.web.commons.utils.RequestHelper.getSessionByAdmin
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminFinder
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminInteraction
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

/**
 * Created by seooseok on 2016. 10. 18..
 */
@RestController
@RequestMapping("/v1/maintenance/management/admin")
class AdminController(
    private val adminInteraction: AdminInteraction,
    private val adminFinder: AdminFinder,
    private val httpSession: HttpSession
) {
    @GetMapping
    fun findOne(): ApiRes<*> {
        val sessionInfo = getSessionByAdmin(httpSession)
        val admin = adminFinder.findBy(sessionInfo.adminId)
        val replyOfAdmin = AdminResources.Reply.from(admin)
        return ApiRes<Any?>(replyOfAdmin)
    }

    // NOTI: admin의 경우 별도 ID를 받지 않는데 이는 profile 수정의 대상은 무조건 로그인 된 대상만 처리 할 수 있도록 하기 위함이다.
    @PutMapping
    fun modifyInfo(
        @RequestBody modify: AdminResources.Modify
    ): ApiRes<*> {
        val sessionInfo = getSessionByAdmin(httpSession)
        adminInteraction.modifyBy(sessionInfo.adminId, modify.imageUrl, modify.description, modify.pw!!)
        return success()
    }
}

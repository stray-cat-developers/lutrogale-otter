package io.mustelidae.otter.lutrogale.api.domain.user.api

import io.mustelidae.otter.lutrogale.api.domain.user.UserManagementInteraction
import io.mustelidae.otter.lutrogale.api.permission.RoleHeader
import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "사용자 관리", description = "이메일 기반 사용자 상태 관리 API")
@LoginCheck(false)
@RestController
@RequestMapping("/v1/users")
class UserManagementController(
    private val userManagementInteraction: UserManagementInteraction,
) {
    @PostMapping("/expire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun expire(
        @RequestHeader(RoleHeader.XSystem.KEY) apiKey: String,
        @RequestBody @Valid body: UserManagementResources.Request.Expire,
    ) {
        userManagementInteraction.expireByEmail(apiKey, body.email)
    }

    @PostMapping("/expire/bulk")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun expireBulk(
        @RequestHeader(RoleHeader.XSystem.KEY) apiKey: String,
        @RequestBody @Valid body: UserManagementResources.Request.BulkExpire,
    ) {
        userManagementInteraction.expireByEmails(apiKey, body.emails)
    }
}

package io.mustelidae.otter.lutrogale.api.domain

import io.mustelidae.otter.lutrogale.api.permission.RoleHeader
import io.mustelidae.otter.lutrogale.common.Replies
import io.mustelidae.otter.lutrogale.common.Reply
import io.mustelidae.otter.lutrogale.common.toReplies
import io.mustelidae.otter.lutrogale.common.toReply
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/sample")
class SampleController {

    @Operation(hidden = true)
    @GetMapping
    fun helloWorld(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
    ): Reply<String> {
        return "Hello World"
            .toReply()
    }

    @Operation(hidden = true)
    @GetMapping("/list")
    fun helloWorld2(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
    ): Replies<String> {
        return listOf("a", "b", "c").toReplies()
    }
}

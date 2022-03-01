package io.mustelidae.otter.lutrogale.api.domain

import io.mustelidae.otter.lutrogale.api.common.Reply
import io.mustelidae.otter.lutrogale.api.common.toReply
import io.mustelidae.otter.lutrogale.api.lock.EnableUserLock
import io.mustelidae.otter.lutrogale.api.permission.RoleHeader
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/sample")
class SampleController {

    @EnableUserLock
    @GetMapping
    fun helloWorld(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long
    ): Reply<String> {
        return "Hello World"
            .toReply()
    }
}

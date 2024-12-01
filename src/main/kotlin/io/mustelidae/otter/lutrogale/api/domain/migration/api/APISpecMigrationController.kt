package io.mustelidae.otter.lutrogale.api.domain.migration.api

import io.mustelidae.otter.lutrogale.web.common.annotation.LoginCheck
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "마이그레이션", description = "OpenAPI Spec 마이그레이션을 수행합니다.")
@LoginCheck(false)
@RestController
@RequestMapping("/v1/migration/project/{projectId}")
class APISpecMigrationController {

    /*@PutMapping("/openapi")
    fun experiment(@PathVariable projectId: Long, @RequestBody request: MigrationResources.Request.OpenAPI): Replies<MenuTreeResources.Reply.TreeBranch> {


        return Replies

    }*/
}

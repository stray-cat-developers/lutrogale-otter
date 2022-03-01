package io.mustelidae.otter.lutrogale.web.domain.admin.api

class AdminResources {

    class Modify(
        val description: String,
        val imageUrl: String,
        val pw: String? = null
    )
}
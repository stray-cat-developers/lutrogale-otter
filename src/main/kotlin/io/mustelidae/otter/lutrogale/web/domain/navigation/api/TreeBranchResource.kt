package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by seooseok on 2016. 10. 19..
 */

class TreeBranchResource(
    @JsonProperty(value = "id")
    val treeId: String,
    @JsonProperty(value = "parent")
    val parentTreeId: String,
    val text: String? = null,
    val type: String? = null,
    @JsonProperty(value = "a_attr")
    val menuNavigationResource: MenuNavigationResource? = null
) {


    companion object {
        fun of(
            treeId: String,
            parentTreeId: String,
            menuNavigationResource: MenuNavigationResource
        ): TreeBranchResource {
            return TreeBranchResource(
                treeId,
                parentTreeId,
                menuNavigationResource.name,
                menuNavigationResource.type.name,
                menuNavigationResource
            )
        }
    }
}

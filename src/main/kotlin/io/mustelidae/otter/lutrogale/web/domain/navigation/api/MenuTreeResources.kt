package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.mustelidae.otter.lutrogale.common.Constant
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.bind.annotation.RequestMethod

class MenuTreeResources {

    class Request {

        @Schema(name = "Lutrogale.MenuTree.Request.Branch")
        class Branch(
            val treeId: String,
            val parentTreeId: String,
            val name: String,
            val uriBlock: String,
            val type: Constant.NavigationType,
            val methodType: RequestMethod,
        ) {
            @JsonIgnore
            fun getRefineUriBlock(): String {
                var block = uriBlock

                if (uriBlock.first() != '/') {
                    block = "/$block"
                }

                if (uriBlock.last() == '/') {
                    block = block.substring(0, block.lastIndex)
                }
                return block
            }
        }
    }

    class Reply {
        @Schema(name = "Lutrogale.MenuTree.Reply.TreeBranch")
        class TreeBranch(
            @Schema(name = "id")
            @JsonProperty(value = "id")
            val treeId: String,
            @Schema(name = "parent")
            @JsonProperty(value = "parent")
            val parentTreeId: String,
            val text: String? = null,
            val type: String? = null,
            @Schema(name = "a_attr")
            @JsonProperty(value = "a_attr")
            val replyOfMenuNavigation: NavigationResources.Reply.ReplyOfMenuNavigation? = null,
        ) {

            companion object {
                fun of(
                    treeId: String,
                    parentTreeId: String,
                    replyOfMenuNavigation: NavigationResources.Reply.ReplyOfMenuNavigation,
                ): TreeBranch {
                    return TreeBranch(
                        treeId,
                        parentTreeId,
                        replyOfMenuNavigation.name,
                        replyOfMenuNavigation.type.name,
                        replyOfMenuNavigation,
                    )
                }
            }
        }
    }
}

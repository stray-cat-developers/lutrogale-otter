package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import com.fasterxml.jackson.annotation.JsonProperty
import io.mustelidae.otter.lutrogale.web.commons.constant.OsoriConstant
import org.springframework.web.bind.annotation.RequestMethod

class MenuTreeResources {

    class Request {

        class Branch(
            val treeId: String,
            val parentTreeId: String,
            val name: String,
            val uriBlock: String,
            val type: OsoriConstant.NavigationType,
            val methodType: RequestMethod
        )
    }

    class Reply {
        class TreeBranch(
            @JsonProperty(value = "id")
            val treeId: String,
            @JsonProperty(value = "parent")
            val parentTreeId: String,
            val text: String? = null,
            val type: String? = null,
            @JsonProperty(value = "a_attr")
            val replyOfMenuNavigation: NavigationResources.Reply.ReplyOfMenuNavigation? = null
        ) {

            companion object {
                fun of(
                    treeId: String,
                    parentTreeId: String,
                    replyOfMenuNavigation: NavigationResources.Reply.ReplyOfMenuNavigation
                ): TreeBranch {
                    return TreeBranch(
                        treeId,
                        parentTreeId,
                        replyOfMenuNavigation.name,
                        replyOfMenuNavigation.type.name,
                        replyOfMenuNavigation
                    )
                }
            }
        }
    }
}

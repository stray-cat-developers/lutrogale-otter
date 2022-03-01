package io.mustelidae.otter.lutrogale.web.domain.navigation.api

import io.mustelidae.smoothcoatedotter.web.commons.constant.OsoriConstant
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Created by HanJaehyun on 2016. 9. 27..
 */
class BranchRequest {
    lateinit var treeId: String
    lateinit var parentTreeId: String
    lateinit var name: String
    lateinit var uriBlock: String
    lateinit var type: OsoriConstant.NavigationType
    lateinit var methodType: RequestMethod
}

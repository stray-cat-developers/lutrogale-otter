package io.mustelidae.otter.lutrogale.common

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.xml.bind.annotation.XmlAnyElement

open class Reply<T>() {
    @get:JsonUnwrapped
    @get:XmlAnyElement
    var content: T? = null

    constructor(content: T) : this() {
        this.content = content
    }

    override fun toString(): String = String.format("Resource { content: %s, %s }", content, super.toString())
}

fun <T> T.toReply(): Reply<T> = Reply(this)

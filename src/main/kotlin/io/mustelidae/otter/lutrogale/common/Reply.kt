package io.mustelidae.otter.lutrogale.common

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.xml.bind.annotation.XmlAnyElement

@Schema(name = "Lutrogale.Common.Reply", description = "Http Json Response Base Format (Class 형태의 리소스를 반환할 때 사용)")
open class Reply<T>() {
    @get:JsonUnwrapped
    @get:XmlAnyElement
    var content: T? = null

    constructor(content: T) : this() {
        this.content = content
    }

    override fun toString(): String {
        return String.format("Resource { content: %s, %s }", content, super.toString())
    }
}

fun <T> T.toReply(): Reply<T> = Reply(this)

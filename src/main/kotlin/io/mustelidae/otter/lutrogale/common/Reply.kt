package io.mustelidae.otter.lutrogale.common

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Common.Reply", description = "Http Json Response Base Format (Class 형태의 리소스를 반환할 때 사용)")
open class Reply<T>() {
    @Schema(name = "result")
    @JsonProperty("result")
    var content: T? = null
    var code: String = "0000"

    constructor(content: T) : this() {
        this.content = content
    }

    override fun toString(): String {
        return String.format("Resource { content: %s, %s }", content, super.toString())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other == null || other.javaClass != javaClass) {
            return false
        }

        val that = other as Reply<*>?

        val contentEqual = if (this.content == null) that!!.content == null else this.content == that!!.content
        return if (contentEqual) super.equals(other) else contentEqual
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result += if (content == null) 0 else 17 * content!!.hashCode()
        return result
    }
}

fun <T> T.toReply(): Reply<T> = Reply(this)

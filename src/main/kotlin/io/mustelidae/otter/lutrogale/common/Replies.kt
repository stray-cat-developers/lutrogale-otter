package io.mustelidae.otter.lutrogale.common

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.util.Assert
import java.util.Collections

@Schema(name = "Lutrogale.Common.Replies")
open class Replies<T>(content: Iterable<T>) : Iterable<T> {

    @JsonIgnore
    private val collection: MutableCollection<T>?

    @Suppress("unused")
    constructor() : this(ArrayList<T>())

    init {
        Assert.notNull(content, "Content must not be null!")

        this.collection = ArrayList()

        for (element in content) {
            this.collection.add(element)
        }
    }

    open fun getContent(): Collection<T> {
        return Collections.unmodifiableCollection(collection!!)
    }

    override fun iterator(): Iterator<T> {
        return collection!!.iterator()
    }

    override fun toString(): String {
        return String.format("Resources { content: %s, %s }", getContent(), super.toString())
    }
}

fun <T> List<T>.toReplies(): Replies<T> = Replies(this)

package io.mustelidae.otter.lutrogale.config.redis

interface RedisKey {
    fun getKey(): String

    companion object {
        const val PREFIX = "lutrogale"
    }
}
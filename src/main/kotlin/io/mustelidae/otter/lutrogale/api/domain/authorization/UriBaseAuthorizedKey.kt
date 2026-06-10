package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.api.domain.authorization.api.AccessResources
import io.mustelidae.otter.lutrogale.config.redis.RedisKey
import java.time.Duration

class UriBaseAuthorizedKey(
    private val apiKey: String,
    private val email: String,
    private val accessUris: List<AccessResources.AccessUri>,
) : RedisKey {
    override fun getKey(): String {
        val sortedUris =
            accessUris
                .map { "${it.methodType}:${it.uri}" }
                .sorted()
                .joinToString("|")
        return "${RedisKey.PREFIX}:authz:uri:$apiKey:$email:$sortedUris"
    }

    companion object {
        val TTL: Duration = Duration.ofMinutes(5)
    }
}

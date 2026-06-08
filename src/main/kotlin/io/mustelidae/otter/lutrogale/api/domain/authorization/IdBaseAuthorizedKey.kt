package io.mustelidae.otter.lutrogale.api.domain.authorization

import io.mustelidae.otter.lutrogale.config.redis.RedisKey
import java.time.Duration

class IdBaseAuthorizedKey(
    private val apiKey: String,
    private val email: String,
    private val menuNavigationIds: List<Long>,
) : RedisKey {
    override fun getKey(): String {
        val sortedIds = menuNavigationIds.sorted().joinToString(",")
        return "${RedisKey.PREFIX}:authz:id:$apiKey:$email:$sortedIds"
    }

    companion object {
        val TTL: Duration = Duration.ofMinutes(5)
    }
}

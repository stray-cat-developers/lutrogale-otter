package io.mustelidae.otter.lutrogale.config.redis.distributedlock

import io.mustelidae.otter.lutrogale.config.redis.RedisKey
import java.time.Duration
import java.util.concurrent.TimeUnit

class DistributedLockKey(
    private val qualifier: String,
) : RedisKey {
    override fun getKey(): String = "${RedisKey.PREFIX}:distributed-lock:$qualifier"

    companion object {
        fun toDuration(
            leaseTime: Long,
            timeUnit: TimeUnit,
        ): Duration = Duration.of(leaseTime, timeUnit.toChronoUnit())
    }
}

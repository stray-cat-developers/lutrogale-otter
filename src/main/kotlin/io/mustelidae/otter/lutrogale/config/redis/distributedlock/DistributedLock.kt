package io.mustelidae.otter.lutrogale.config.redis.distributedlock

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    val qualifier: String,
    val leaseTime: Long = 10L,
    val timeUnit: TimeUnit = TimeUnit.MINUTES,
)

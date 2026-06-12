package io.mustelidae.otter.lutrogale.config.redis.distributedlock

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.UUID

@Aspect
@Component
class DistributedLockAspect(
    private val redisTemplate: StringRedisTemplate,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val unlockScript =
        DefaultRedisScript(
            """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            else
                return 0
            end
            """.trimIndent(),
            Long::class.java,
        )

    // Parameter name "distributedLock" must match the pointcut expression to bind the annotation instance
    @Around("@annotation(distributedLock)")
    fun around(
        pjp: ProceedingJoinPoint,
        distributedLock: DistributedLock,
    ): Any? {
        val key = DistributedLockKey(distributedLock.qualifier).getKey()
        val lockValue = UUID.randomUUID().toString()
        val duration = DistributedLockKey.toDuration(distributedLock.leaseTime, distributedLock.timeUnit)

        val acquired = tryAcquire(key, lockValue, duration)

        if (acquired == null) {
            log.warn("Redis 연결 실패로 분산락을 건너뜁니다. key={}", key)
            return pjp.proceed()
        }

        if (!acquired) {
            log.info("분산락 획득 실패, 실행을 건너뜁니다. key={}", key)
            return null
        }

        log.debug("분산락 획득. key={}", key)
        try {
            return pjp.proceed()
        } finally {
            release(key, lockValue)
        }
    }

    /** @return true=획득 성공, false=다른 서버 점유 중, null=Redis 오류 */
    private fun tryAcquire(
        key: String,
        lockValue: String,
        duration: Duration,
    ): Boolean? =
        try {
            redisTemplate.opsForValue().setIfAbsent(key, lockValue, duration)
        } catch (e: Exception) {
            log.warn("Redis 락 획득 중 오류. key={}", key, e)
            null
        }

    private fun release(
        key: String,
        lockValue: String,
    ) {
        try {
            redisTemplate.execute(unlockScript, listOf(key), lockValue)
        } catch (e: Exception) {
            log.warn("Redis 락 해제 중 오류. key={}", key, e)
        }
    }
}

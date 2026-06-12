package io.mustelidae.otter.lutrogale.config.redis.distributedlock

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.aspectj.lang.ProceedingJoinPoint
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.core.script.DefaultRedisScript
import java.util.concurrent.TimeUnit

class DistributedLockAspectTest {
    private lateinit var redisTemplate: StringRedisTemplate
    private lateinit var valueOps: ValueOperations<String, String>
    private lateinit var pjp: ProceedingJoinPoint
    private lateinit var aspect: DistributedLockAspect
    private lateinit var lock: DistributedLock

    @BeforeEach
    fun setUp() {
        redisTemplate = mockk()
        valueOps = mockk()
        pjp = mockk()
        aspect = DistributedLockAspect(redisTemplate)
        lock = mockk()

        every { redisTemplate.opsForValue() } returns valueOps
        every { lock.qualifier } returns "test-qualifier"
        every { lock.leaseTime } returns 10L
        every { lock.timeUnit } returns TimeUnit.MINUTES
    }

    @Test
    fun `락 획득 성공 시 proceed가 실행되고 결과를 반환한다`() {
        every { valueOps.setIfAbsent(any(), any(), any()) } returns true
        every { redisTemplate.execute(any<DefaultRedisScript<Long>>(), any<List<String>>(), any()) } returns 1L
        every { pjp.proceed() } returns "result"

        val result = aspect.around(pjp, lock)

        result shouldBe "result"
        verify(exactly = 1) { pjp.proceed() }
        verify(exactly = 1) { redisTemplate.execute(any(), any<List<String>>(), any()) }
    }

    @Test
    fun `락 획득 실패 시 proceed가 실행되지 않고 null을 반환한다`() {
        every { valueOps.setIfAbsent(any(), any(), any()) } returns false

        val result = aspect.around(pjp, lock)

        result shouldBe null
        verify(exactly = 0) { pjp.proceed() }
    }

    @Test
    fun `Redis 연결 실패 시 fail-open으로 proceed가 실행된다`() {
        every { valueOps.setIfAbsent(any(), any(), any()) } throws RuntimeException("Redis connection failed")
        every { pjp.proceed() } returns "fallback-result"

        val result = aspect.around(pjp, lock)

        result shouldBe "fallback-result"
        verify(exactly = 1) { pjp.proceed() }
    }

    @Test
    fun `proceed에서 예외가 발생해도 finally에서 동일한 lock value로 unlock이 실행된다`() {
        val setValueSlot = slot<String>()
        val executeArgSlot = slot<String>()
        every { valueOps.setIfAbsent(any(), capture(setValueSlot), any()) } returns true
        every { redisTemplate.execute(any<DefaultRedisScript<Long>>(), any<List<String>>(), capture(executeArgSlot)) } returns 1L
        every { pjp.proceed() } throws RuntimeException("business logic failed")

        assertThrows<RuntimeException> { aspect.around(pjp, lock) }

        verify(exactly = 1) { redisTemplate.execute(any(), any<List<String>>(), any()) }
        executeArgSlot.captured shouldBe setValueSlot.captured
    }

    @Test
    fun `qualifier로 올바른 Redis key가 생성된다`() {
        val capturedKey = slot<String>()
        every { valueOps.setIfAbsent(capture(capturedKey), any(), any()) } returns false

        aspect.around(pjp, lock)

        capturedKey.captured shouldBe "lutrogale:distributed-lock:test-qualifier"
    }
}

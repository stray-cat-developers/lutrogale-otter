package io.mustelidae.otter.lutrogale.config

import com.github.fppt.jedismock.RedisServer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@TestConfiguration(proxyBeanMethods = false)
class TestRedisConfiguration {
    @Bean(destroyMethod = "stop")
    fun redisServer(): RedisServer = RedisServer.newRedisServer().start()

    @Bean
    @Primary
    fun redisConnectionFactory(redisServer: RedisServer): LettuceConnectionFactory =
        LettuceConnectionFactory(RedisStandaloneConfiguration(redisServer.host, redisServer.bindPort))
}

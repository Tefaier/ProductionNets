package com.prod.nets

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import java.util.UUID


@Configuration
class MainConfiguration {
    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        return JedisConnectionFactory()
    }

    @Bean
    fun redisTemplate(): RedisTemplate<UUID, Any> {
        val template = RedisTemplate<UUID, Any>()
        template.connectionFactory = jedisConnectionFactory()
        return template
    }
}
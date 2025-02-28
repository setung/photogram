package com.setung.client

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisClient(
    private val redisTemplate: StringRedisTemplate
) {

    fun save(key: String, value: String, expireMinutes: Long = 10) {
        redisTemplate.opsForValue().set(key, value, expireMinutes, TimeUnit.MINUTES)
    }

    fun get(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    fun deleteEmailCode(key: String) {
        redisTemplate.delete(key)
    }
}
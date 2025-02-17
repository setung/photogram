package com.setung.userservice.service

import com.setung.userservice.client.RedisClient
import com.setung.userservice.entity.EmailCodeType
import com.setung.userservice.error.InvalidEmailCodeException
import com.setung.userservice.error.NotFoundRedisDataException
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class EmailCodeService(
    private val redisClient: RedisClient
) {

    fun generateEmailCode(email: String, type: EmailCodeType): String {
        val code = String.format("%06d", Random.nextInt(0, 1000000))
        redisClient.save(getKey(email, type), code, 3)
        return code
    }

    fun verifyEmailCode(email: String, code: String, type: EmailCodeType) {
        val key = getKey(email, type)
        val storedCode = redisClient.get(key) ?: throw NotFoundRedisDataException(key)

        if (storedCode != code) throw InvalidEmailCodeException()

        redisClient.deleteEmailCode(key)
    }

    private fun getKey(email: String, type: EmailCodeType) = "emailCode:$email:${type.name}"
}

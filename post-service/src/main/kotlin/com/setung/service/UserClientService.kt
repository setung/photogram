package com.setung.service

import com.setung.client.UserClient
import com.setung.client.UserDto
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class UserClientService(
    private val userClient: UserClient
) {

    @CircuitBreaker(name = "userClient", fallbackMethod = "fallbackUser")
    @Cacheable(cacheNames = ["post_userCache"], key = "#loginUserId + ':' + #writerId")
    fun getUser(loginUserId: Long, writerId: Long): UserDto {
        return userClient.getUser(loginUserId, writerId)
    }

    fun fallbackUser(loginUserId: Long, writerId: Long, ex: Throwable): UserDto {
        ex.printStackTrace()
        return UserDto.ofPrivate(writerId)
    }
}
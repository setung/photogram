package com.setung.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import java.io.Serializable

@FeignClient("user-service")
@Component
interface UserClient {

    @GetMapping("/users/{userId}")
    fun getUser(@RequestHeader("user-id") loginUserId: Long, @PathVariable userId: Long): UserDto

    @GetMapping("/users/{userId}/followers")
    fun getUserFollowers(@PathVariable userId: Long): List<Long>
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserDto(
    val id: Long,
    val email: String?,
    val name: String?,
    val isVisible: Boolean,
    val biography: String?,
) : Serializable {
    companion object {
        fun ofPrivate(id: Long) = UserDto(
            id = id,
            email = null,
            name = null,
            isVisible = false,
            biography = null,
        )
    }
}
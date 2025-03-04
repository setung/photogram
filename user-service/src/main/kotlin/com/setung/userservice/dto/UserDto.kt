package com.setung.userservice.dto

import com.setung.userservice.entity.User
import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val email: String?,
    val name: String,
    val biography: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {

        fun of(user: User) =
            if (user.isPrivate) ofPrivateUser(user) else ofPublicUser(user)

        fun ofPublicUser(user: User) = UserDto(
            id = user.id!!,
            email = user.email,
            name = user.name,
            biography = user.biography,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )

        fun ofPrivateUser(user: User) = UserDto(
            id = user.id!!,
            name = user.name,
            email = null,
            biography = null,
            createdAt = null,
            updatedAt = null
        )
    }
}

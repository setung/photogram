package com.setung.userservice.repo

import com.setung.userservice.entity.UserEntity
import com.setung.userservice.entity.UserStatus
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {

    fun existsByEmailAndStatus(email: String, normal: UserStatus): Boolean

    fun findByEmailAndStatus(email: String, status: UserStatus): UserEntity?

    fun findByIdAndStatus(id: Long, status: UserStatus): UserEntity?
}
package com.setung.repo

import com.setung.entity.UserEntity
import com.setung.entity.UserStatus
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {

    fun existsByEmailAndStatus(email: String, normal: UserStatus): Boolean

    fun findByEmailAndStatus(email: String, status: UserStatus): UserEntity?

    fun findByIdAndStatus(id: Long, status: UserStatus): UserEntity?
}
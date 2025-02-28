package com.setung.repo

import com.setung.entity.User
import com.setung.entity.UserStatus
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun existsByEmailAndStatus(email: String, normal: UserStatus): Boolean

    fun findByEmailAndStatus(email: String, status: UserStatus): User?

    fun findByIdAndStatus(id: Long, status: UserStatus): User?
}
package com.setung.userservice.repo

import com.setung.userservice.entity.User
import com.setung.userservice.entity.UserStatus
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun existsByEmailAndStatus(email: String, normal: UserStatus): Boolean

    fun findByEmailAndStatus(email: String, status: UserStatus): User?

}
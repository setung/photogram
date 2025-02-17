package com.setung.userservice.entity

import com.setung.userservice.dto.UserSignupRequest
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    var name: String,

    val email: String,

    val password: String

) : BaseEntity() {
    companion object {
        fun of(request: UserSignupRequest, encryptedPassword: String) = User(
            id = null,
            name = request.name,
            email = request.email,
            password = encryptedPassword
        )
    }
}
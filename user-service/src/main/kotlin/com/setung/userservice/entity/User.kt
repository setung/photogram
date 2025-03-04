package com.setung.userservice.entity

import com.setung.userservice.dto.UserSignupRequest
import com.setung.userservice.dto.UserUpdateRequest
import jakarta.persistence.*

@Entity
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    var name: String,

    val email: String,

    var password: String,

    @Enumerated(EnumType.STRING)
    var status: UserStatus,

    var biography: String?,

    var isPrivate: Boolean,

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    var profileImage: ProfileImage? = null

) : BaseEntity() {

    fun update(request: UserUpdateRequest) {
        name = request.name
        biography = request.biography
        isPrivate = request.isPrivate
    }

    fun updatePassword(password: String) {
        this.password = password
    }

    fun delete() {
        status = UserStatus.DELETED
    }

    fun uploadProfileImage(profileImage: ProfileImage) {
        this.profileImage = profileImage
    }

    fun deleteProfileImage() {
        this.profileImage = null
    }

    companion object {
        fun of(request: UserSignupRequest, encryptedPassword: String) = User(
            id = null,
            name = request.name,
            email = request.email,
            password = encryptedPassword,
            status = UserStatus.NORMAL,
            biography = null,
            isPrivate = false
        )
    }
}
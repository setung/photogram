package com.setung.userservice.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserSignupRequest(

    @field:Email
    val email: String,

    @field:NotBlank
    val name: String,

    @field:NotBlank
    var password: String,

    @field:NotBlank
    val code: String
)

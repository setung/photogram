package com.setung.userservice.dto

import jakarta.validation.constraints.NotNull

data class PasswordUpdateRequest(

    @field:NotNull
    val password: String,

    @field:NotNull
    val code: String
)
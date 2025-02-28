package com.setung.dto

import jakarta.validation.constraints.NotNull

data class PasswordUpdateRequest(

    @field:NotNull
    val password: String,

    @field:NotNull
    val code: String
)
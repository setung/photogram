package com.setung.userservice.dto

import jakarta.validation.constraints.NotNull

data class UserUpdateRequest(

    @field:NotNull
    val name: String,

    @field:NotNull
    val biography: String,

    @field:NotNull
    val isPrivate: Boolean
)
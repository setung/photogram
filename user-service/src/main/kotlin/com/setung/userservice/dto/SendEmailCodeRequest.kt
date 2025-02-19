package com.setung.userservice.dto

import com.setung.userservice.entity.EmailCodeType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

data class SendEmailCodeRequest(

    @field:Email
    val email: String,

    @field:NotNull
    val type: EmailCodeType
)

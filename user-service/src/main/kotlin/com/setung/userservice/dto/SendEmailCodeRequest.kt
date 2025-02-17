package com.setung.userservice.dto

import com.setung.userservice.entity.EmailCodeType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SendEmailCodeRequest(

    @field:Email
    val email: String,

    @field:NotBlank
    val type: EmailCodeType
)

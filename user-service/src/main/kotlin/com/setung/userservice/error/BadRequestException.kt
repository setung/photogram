package com.setung.userservice.error

open class BadRequestException(message: String) : RuntimeException(message)

class SelfFollowException(message: String) : BadRequestException(message)

class InvalidEmailCodeException : BadRequestException("Invalid verification code.")

package com.setung.userservice.error

open class UnAuthorizationException(message: String) : RuntimeException(message)

class InvalidPasswordException : UnAuthorizationException("Invalid password.")
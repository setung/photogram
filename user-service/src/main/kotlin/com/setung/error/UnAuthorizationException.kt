package com.setung.error

open class UnAuthorizationException(message: String) : RuntimeException(message)

class InvalidPasswordException : UnAuthorizationException("Invalid password.")
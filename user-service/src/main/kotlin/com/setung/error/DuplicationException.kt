package com.setung.error

open class DuplicationException(message: String) : RuntimeException(message)

class DuplicateEmailException(email: String) : DuplicationException("Email is already in use: $email")

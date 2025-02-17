package com.setung.userservice.error

open class NotFoundException (message: String) : RuntimeException(message)

class NotFoundRedisDataException(key: String) : NotFoundException("Not found redis value of: $key")

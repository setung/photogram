package com.setung.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(
        MethodArgumentNotValidException::class,
        HttpMessageNotReadableException::class,
        InvalidEmailCodeException::class,
        BadRequestException::class
    )
    fun handleValidationException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(
        DuplicationException::class,
    )
    fun duplicationException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(
        NotFoundException::class,
    )
    fun notFoundException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(
        UnAuthorizationException::class,
        HttpClientErrorException.Unauthorized::class
    )
    fun unAuthorizationException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(
        ForbiddenException::class,
    )
    fun forbiddenException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.FORBIDDEN)
    }
}
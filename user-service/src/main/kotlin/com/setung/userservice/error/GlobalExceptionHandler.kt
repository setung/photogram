package com.setung.userservice.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(
        MethodArgumentNotValidException::class,
        HttpMessageNotReadableException::class,
        InvalidEmailCodeException::class
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
}
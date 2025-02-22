package com.setung.userservice.controller

import com.setung.userservice.dto.LoginRequest
import com.setung.userservice.dto.SendEmailCodeRequest
import com.setung.userservice.dto.UserSignupRequest
import com.setung.userservice.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    val userService: UserService
) {

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: UserSignupRequest) =
        ResponseEntity(userService.signup(request), HttpStatus.OK)

    @PostMapping("/emails/send-code")
    fun sendEmailCode(@Valid @RequestBody request: SendEmailCodeRequest) =
        ResponseEntity(userService.sendEmailCode(request), HttpStatus.OK)

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest) =
        ResponseEntity(userService.login(request), HttpStatus.OK)

    @GetMapping("/{userId}")
    fun get(@PathVariable userId: Long) =
        ResponseEntity(userService.findById(userId), HttpStatus.OK)
}
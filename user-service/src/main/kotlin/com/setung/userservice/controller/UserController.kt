package com.setung.userservice.controller

import com.setung.userservice.dto.SendEmailCodeRequest
import com.setung.userservice.dto.UserSignupRequest
import com.setung.userservice.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    val userService: UserService
) {

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: UserSignupRequest) =
        ResponseEntity(userService.signup(request), HttpStatus.OK)

    @PostMapping("/emails/send-code")
    fun sendEmailCode(@RequestBody request: SendEmailCodeRequest) =
        ResponseEntity(userService.sendEmailCode(request), HttpStatus.OK)
}
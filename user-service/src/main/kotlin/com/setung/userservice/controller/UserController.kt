package com.setung.userservice.controller

import com.setung.userservice.annotation.LoginUser
import com.setung.userservice.dto.*
import com.setung.userservice.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

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

    @GetMapping("/me")
    fun findMe(@LoginUser userId: Long) =
        ResponseEntity(userService.findMe(userId), HttpStatus.OK)

    @GetMapping("/{userId}")
    fun findUser(@PathVariable userId: Long) =
        ResponseEntity(userService.findUser(userId), HttpStatus.OK)

    @PatchMapping("/me")
    fun update(@RequestBody request: UserUpdateRequest, @LoginUser userId: Long) =
        ResponseEntity(userService.update(userId, request), HttpStatus.OK)

    @PatchMapping("/me/password")
    fun updatePassword(@RequestBody request: PasswordUpdateRequest, @LoginUser userId: Long) =
        ResponseEntity(userService.updatePassword(userId, request), HttpStatus.OK)

    @DeleteMapping("/me")
    fun delete(@RequestBody request: UserDeleteRequest, @LoginUser userId: Long) =
        ResponseEntity(userService.delete(userId, request), HttpStatus.OK)

    @PostMapping("/me/profile-image")
    fun uploadProfileImage(@LoginUser userId: Long, @RequestPart file: MultipartFile) =
        ResponseEntity(userService.uploadProfileImage(userId, file), HttpStatus.OK)

    @DeleteMapping("/me/profile-image")
    fun deleteProfileImage(@LoginUser userId: Long) =
        ResponseEntity(userService.deleteProfileImage(userId), HttpStatus.OK)

}
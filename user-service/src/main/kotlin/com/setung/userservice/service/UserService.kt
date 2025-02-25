package com.setung.userservice.service

import com.setung.auth.jwt.JwtProvider
import com.setung.userservice.client.EmailClient
import com.setung.userservice.dto.*
import com.setung.userservice.entity.EmailCodeType
import com.setung.userservice.entity.User
import com.setung.userservice.entity.UserStatus
import com.setung.userservice.error.DuplicateEmailException
import com.setung.userservice.error.InvalidPasswordException
import com.setung.userservice.error.NotFoundException
import com.setung.userservice.repo.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val emailCodeService: EmailCodeService,
    val emailService: EmailClient,
    val jwtProvider: JwtProvider
) {

    fun signup(request: UserSignupRequest): Long {
        if (userRepository.existsByEmailAndStatus(
                request.email,
                UserStatus.NORMAL
            )
        ) throw DuplicateEmailException(request.email)

        emailCodeService.verifyEmailCode(request.email, request.code, EmailCodeType.SIGNUP)

        val user: User = User.of(request, passwordEncoder.encode(request.password))

        return userRepository.save(user).id ?: throw IllegalStateException("Failed to save user")
    }

    fun sendEmailCode(request: SendEmailCodeRequest) {
        val code = emailCodeService.generateEmailCode(request.email, request.type)
        emailService.send(request.email, code)
    }

    fun findById(id: Long): User =
        userRepository.findById(id)
            .orElseThrow { NotFoundException("Could not find user with id ${id}") }

    fun login(request: LoginRequest): String {
        val user = userRepository.findByEmailAndStatus(request.email, UserStatus.NORMAL)
            ?: throw NotFoundException("Could not find user with email ${request.email}")

        if (!passwordEncoder.matches(request.password, user.password))
            throw InvalidPasswordException()

        return jwtProvider.createToken(user.id!!)
    }

    fun update(id: Long, request: UserUpdateRequest) {
        val user = findById(id)
        user.update(request)
        userRepository.save(user)
    }

    fun updatePassword(id: Long, request: PasswordUpdateRequest) {
        val user = findById(id)
        val encryptedPassword = passwordEncoder.encode(request.password)
        emailCodeService.verifyEmailCode(user.email, request.code, EmailCodeType.PASSWORD_RESET)

        user.updatePassword(encryptedPassword)

        userRepository.save(user)
    }
}
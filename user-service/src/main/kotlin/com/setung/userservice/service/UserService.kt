package com.setung.userservice.service

import com.setung.userservice.client.EmailClient
import com.setung.userservice.dto.SendEmailCodeRequest
import com.setung.userservice.dto.UserSignupRequest
import com.setung.userservice.entity.EmailCodeType
import com.setung.userservice.entity.User
import com.setung.userservice.error.DuplicateEmailException
import com.setung.userservice.error.NotFoundException
import com.setung.userservice.repo.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val emailCodeService: EmailCodeService,
    val emailService: EmailClient
) {

    @Transactional
    fun signup(request: UserSignupRequest): Long {
        if (userRepository.existsByEmail(request.email)) throw DuplicateEmailException(request.email)

        emailCodeService.verifyEmailCode(request.email, request.code, EmailCodeType.SIGNUP)

        val user: User = User.of(request, passwordEncoder.encode(request.password))

        return userRepository.save(user).id ?: throw IllegalStateException("Failed to save user")
    }

    @Transactional
    fun sendEmailCode(request: SendEmailCodeRequest) {
        val code = emailCodeService.generateEmailCode(request.email, request.type)
        emailService.send(request.email, code)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): User =
        userRepository.findById(id).orElseThrow { NotFoundException("Could not find user with id ${id}") }
}
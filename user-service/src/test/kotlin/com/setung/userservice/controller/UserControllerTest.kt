package com.setung.userservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.setung.userservice.config.SecurityConfig
import com.setung.userservice.dto.SendEmailCodeRequest
import com.setung.userservice.dto.UserSignupRequest
import com.setung.userservice.entity.EmailCodeType
import com.setung.userservice.error.DuplicateEmailException
import com.setung.userservice.error.InvalidEmailCodeException
import com.setung.userservice.error.NotFoundRedisDataException
import com.setung.userservice.service.UserService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserController::class)
@Import(SecurityConfig::class)
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    lateinit var userService: UserService

    @Nested
    inner class SignUpTest {

        @Test
        @DisplayName("회원가입 성공")
        fun signUpSuccess() {
            val request = UserSignupRequest("test@example.com", "password", "password", "code")
            given(userService.signup(request)).willReturn(1L)

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/users/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").value(1L))
        }

        @Test
        @DisplayName("회원가입 실패 - 이메일 중복")
        fun signUpFailDuplicateEmail() {
            val request =
                UserSignupRequest("duplicated@example.com", "password", "password", "code")
            given(userService.signup(request)).willThrow(DuplicateEmailException("duplicated email"))

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/users/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isConflict)
        }

        @Test
        @DisplayName("회원가입 실패 - 값 입력 안함")
        fun signUpFailBlankPassword() {
            val request = UserSignupRequest("duplicated@example.com", "name", "", "code")
            given(userService.signup(request)).willThrow(DuplicateEmailException("blank password"))

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/users/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        @DisplayName("회원가입 실패 - 생성되지 않은 입력 코드로 시도")
        fun signUpFailInvalidCode() {
            val request =
                UserSignupRequest("duplicated@example.com", "password", "password", "wrong-code")
            given(userService.signup(request)).willThrow(NotFoundRedisDataException("not found key"))

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/users/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isNotFound)
        }

        @Test
        @DisplayName("회원가입 실패 - 인증 코드 다름")
        fun signUpFailMismatchedCode() {
            val request =
                UserSignupRequest("duplicated@example.com", "password", "password", "wrong-code")
            given(userService.signup(request)).willThrow(InvalidEmailCodeException())

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/users/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    inner class EmailCodeTest {
        @Test
        @DisplayName("이메일 인증 코드 전송")
        fun sendEmailCode() {
            val request = SendEmailCodeRequest("test@example.com", EmailCodeType.SIGNUP)
            doNothing().`when`(userService).sendEmailCode(request)

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/users/emails/send-code")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
        }
    }
}

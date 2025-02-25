package com.setung.userservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.setung.userservice.config.SecurityConfig
import com.setung.userservice.dto.*
import com.setung.userservice.entity.EmailCodeType
import com.setung.userservice.error.*
import com.setung.userservice.service.EmailCodeService
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
    private lateinit var emailCodeService: EmailCodeService

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

    @Nested
    inner class LoginTest {

        @Test
        @DisplayName(" 로그인 성공 테스트")
        fun loginSuccessTest() {
            val request = LoginRequest("test@test.com", "1234")
            given(userService.login(request)).willReturn("jwt-token")

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isOk)
                .andExpect(jsonPath("$").value("jwt-token"))
        }

        @Test
        @DisplayName(" 로그인 실패 테스트 - 가입되지 않은 이메일")
        fun loginFailureTestWithNotJoinedEmail() {
            val request = LoginRequest("no_joined@test.com", "1234")
            given(userService.login(request)).willThrow(NotFoundException("not found email"))

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isNotFound)
        }

        @Test
        @DisplayName(" 로그인 실패 테스트 - 잘못된 비밀번호")
        fun loginFailureTestWithWrongPassword() {
            val request = LoginRequest("Wrong_password@test.com", "1234")
            given(userService.login(request)).willThrow(InvalidPasswordException())

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isUnauthorized)
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        @DisplayName("[200] 유저 업데이트 성공 테스트")
        fun userUpdateSuccessTest() {
            val request = UserUpdateRequest("name", "biography", false)
            doNothing().`when`(userService).update(1L, request)

            mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/users/me")
                    .header("user-id", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isOk)
        }

        @Test
        @DisplayName("[401] 유저 업데이트 실패 테스트 - user-id 헤더가 없는 경우")
        fun userUpdateFailureTestWithoutUserIdHeaderTest() {
            val request = UserUpdateRequest("name", "biography", false)
            doNothing().`when`(userService).update(1L, request)

            mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/users/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isUnauthorized)
        }

        @Test
        @DisplayName("[200] 패스워드 업데이트 성공 테스트")
        fun passwordUpdateSuccessTest() {
            val request = PasswordUpdateRequest("password", "code")
            doNothing().`when`(userService).updatePassword(1, request)

            mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/users/me/password")
                    .header("user-id", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isOk)
        }

        @Test
        @DisplayName("[401] 패스워드 업데이트 실패 테스트 - user-id 헤더가 없는 경우")
        fun passwordUpdateFailureTestWithoutUserIdHeaderTest() {
            val request = PasswordUpdateRequest("password", "code")
            doNothing().`when`(userService).updatePassword(1, request)

            mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/users/me/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isUnauthorized)
        }

        @Test
        @DisplayName("[400] 패스워드 업데이트 실패 테스트 - emailCode가 유효하지않은 경우")
        fun passwordUpdateFailureT2estWithoutUserIdHeaderTest() {
            val request = PasswordUpdateRequest("password", "wrong-code")
            given(userService.updatePassword(1, request)).willThrow(InvalidEmailCodeException())
            mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/users/me/password")
                    .header("user-id", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    inner class DeleteTest {

        @Test
        @DisplayName("[200] 삭제 성공")
        fun deleteSuccessTest() {
            val request = UserDeleteRequest("delete-code")
            doNothing().`when`(userService).delete(1, request)

            mockMvc.perform(
                MockMvcRequestBuilders
                    .delete("/users/me")
                    .header("user-id", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
        }

        @Test
        @DisplayName("[400] 삭제 실패 - 잘못된 인증 코드")
        fun deleteFailureTestWithInvalidEmailCode() {
            val request = UserDeleteRequest("delete-code")
            given(userService.delete(1, request)).willThrow(InvalidEmailCodeException())

            mockMvc.perform(
                MockMvcRequestBuilders
                    .delete("/users/me")
                    .header("user-id", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        @DisplayName("[404] 삭제 후 계정 조회 테스트")
        fun findDeletedUserFailureTest() {
            given(userService.findById(1)).willThrow(NotFoundException("Could not find user"))

            mockMvc.perform(
                MockMvcRequestBuilders
                    .get("/users/1")
                    .header("user-id", 1)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound)
        }
    }
}

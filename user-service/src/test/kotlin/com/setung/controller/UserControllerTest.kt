package com.setung.controller

import com.setung.auth.constant.LoginStatus
import com.setung.dto.*
import com.setung.entity.EmailCodeType
import com.setung.error.*
import com.setung.service.UserService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

class UserControllerTest : AbstractControllerTest() {

    var userService: UserService = Mockito.mock()

    @Nested
    inner class SignUpTest {

        @Test
        @DisplayName("회원가입 성공")
        fun signUpSuccess() {
            val request = UserSignupRequest("test@example.com", "password", "password", "code")
            given(userService.signup(request)).willReturn(1L)

            mockMvc().perform(
                MockMvcRequestBuilders.post("/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk).andExpect(jsonPath("$").value(1L))
        }

        @Test
        @DisplayName("회원가입 실패 - 이메일 중복")
        fun signUpFailDuplicateEmail() {
            val request = UserSignupRequest("duplicated@example.com", "password", "password", "code")
            given(userService.signup(request)).willThrow(DuplicateEmailException("duplicated email"))

            mockMvc().perform(
                MockMvcRequestBuilders.post("/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isConflict)
        }

        @Test
        @DisplayName("회원가입 실패 - 값 입력 안함")
        fun signUpFailBlankPassword() {
            val request = UserSignupRequest("duplicated@example.com", "name", "", "code")
            given(userService.signup(request)).willThrow(DuplicateEmailException("blank password"))

            mockMvc().perform(
                MockMvcRequestBuilders.post("/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest)
        }

        @Test
        @DisplayName("회원가입 실패 - 생성되지 않은 입력 코드로 시도")
        fun signUpFailInvalidCode() {
            val request = UserSignupRequest("duplicated@example.com", "password", "password", "wrong-code")
            given(userService.signup(request)).willThrow(NotFoundRedisDataException("not found key"))

            mockMvc().perform(
                MockMvcRequestBuilders.post("/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isNotFound)
        }

        @Test
        @DisplayName("회원가입 실패 - 인증 코드 다름")
        fun signUpFailMismatchedCode() {
            val request = UserSignupRequest("duplicated@example.com", "password", "password", "wrong-code")
            given(userService.signup(request)).willThrow(InvalidEmailCodeException())

            mockMvc().perform(
                MockMvcRequestBuilders.post("/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest)
        }
    }

    @Nested
    inner class EmailCodeTest {
        @Test
        @DisplayName("이메일 인증 코드 전송")
        fun sendEmailCode() {
            val request = SendEmailCodeRequest("test@example.com", EmailCodeType.SIGNUP)
            doNothing().`when`(userService).sendEmailCode(request)

            mockMvc().perform(
                MockMvcRequestBuilders.post("/users/emails/send-code").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk)
        }
    }

    @Nested
    inner class LoginTest {

        @Test
        @DisplayName(" 로그인 성공 테스트")
        fun loginSuccessTest() {
            val request = LoginRequest("test@test.com", "1234")
            given(userService.login(request)).willReturn("jwt-token")

            mockMvc().perform(
                MockMvcRequestBuilders.post("/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isOk).andExpect(jsonPath("$").value("jwt-token"))
        }

        @Test
        @DisplayName(" 로그인 실패 테스트 - 가입되지 않은 이메일")
        fun loginFailureTestWithNotJoinedEmail() {
            val request = LoginRequest("no_joined@test.com", "1234")
            given(userService.login(request)).willThrow(NotFoundException("not found email"))

            mockMvc().perform(
                MockMvcRequestBuilders.post("/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isNotFound)
        }

        @Test
        @DisplayName(" 로그인 실패 테스트 - 잘못된 비밀번호")
        fun loginFailureTestWithWrongPassword() {
            val request = LoginRequest("Wrong_password@test.com", "1234")
            given(userService.login(request)).willThrow(InvalidPasswordException())

            mockMvc().perform(
                MockMvcRequestBuilders.post("/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
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

            mockMvc().perform(
                MockMvcRequestBuilders.patch("/users/me").header("user-id", 1).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isOk)
        }

        @Test
        @DisplayName("[401] 유저 업데이트 실패 테스트 - user-id 헤더가 없는 경우")
        fun userUpdateFailureTestWithoutUserIdHeaderTest() {
            val request = UserUpdateRequest("name", "biography", false)
            doNothing().`when`(userService).update(1L, request)

            mockMvc().perform(
                MockMvcRequestBuilders.patch("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isUnauthorized)
        }

        @Test
        @DisplayName("[200] 패스워드 업데이트 성공 테스트")
        fun passwordUpdateSuccessTest() {
            val request = PasswordUpdateRequest("password", "code")
            doNothing().`when`(userService).updatePassword(1, request)

            mockMvc().perform(
                MockMvcRequestBuilders.patch("/users/me/password").header("user-id", 1).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isOk)
        }

        @Test
        @DisplayName("[401] 패스워드 업데이트 실패 테스트 - user-id 헤더가 없는 경우")
        fun passwordUpdateFailureTestWithoutUserIdHeaderTest() {
            val request = PasswordUpdateRequest("password", "code")
            doNothing().`when`(userService).updatePassword(1, request)

            mockMvc().perform(
                MockMvcRequestBuilders.patch("/users/me/password").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )

                .andExpect(status().isUnauthorized)
        }

        @Test
        @DisplayName("[400] 패스워드 업데이트 실패 테스트 - emailCode가 유효하지않은 경우")
        fun passwordUpdateFailureT2estWithoutUserIdHeaderTest() {
            val request = PasswordUpdateRequest("password", "wrong-code")
            given(userService.updatePassword(1, request)).willThrow(InvalidEmailCodeException())
            mockMvc().perform(
                MockMvcRequestBuilders.patch("/users/me/password").header("user-id", 1).contentType(MediaType.APPLICATION_JSON)
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

            mockMvc().perform(
                MockMvcRequestBuilders.delete("/users/me").header("user-id", 1).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk)
        }

        @Test
        @DisplayName("[400] 삭제 실패 - 잘못된 인증 코드")
        fun deleteFailureTestWithInvalidEmailCode() {
            val request = UserDeleteRequest("delete-code")
            given(userService.delete(1, request)).willThrow(InvalidEmailCodeException())

            mockMvc().perform(
                MockMvcRequestBuilders.delete("/users/me").header("user-id", 1).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest)
        }

        @Test
        @DisplayName("[404] 삭제 후 계정 조회 테스트")
        fun findDeletedUserFailureTest() {
            given(userService.findUser(LoginStatus.ANONYMOUS.id, 1)).willThrow(NotFoundException("Could not find user"))

            mockMvc().perform(
                MockMvcRequestBuilders.get("/users/1")
                    .header("user-id", LoginStatus.ANONYMOUS.id)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class FindTest {

        @Test
        @DisplayName("[200] 자신 조회")
        fun findMeTest() {
            given(userService.findMe(1)).willReturn(
                UserDto(
                    id = 1L,
                    name = "name",
                    email = "tester@test.com",
                    biography = "biography",
                    isVisible = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )

            mockMvc().perform(
                MockMvcRequestBuilders.get("/users/me").header("user-id", 1).contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk)
        }

        @Test
        @DisplayName("[401] 자신 조회 - userId 헤더 없음")
        fun findMeFailureTestWithoutUserIdHeader() {
            mockMvc().perform(
                MockMvcRequestBuilders.get("/users/me").contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized)
        }

        @Test
        @DisplayName("[200] 유저 조회")
        fun findUserTest() {
            given(userService.findUser(LoginStatus.ANONYMOUS.id, 1)).willReturn(
                UserDto(
                    id = 1L,
                    name = "name",
                    email = "tester@test.com",
                    biography = "biography",
                    isVisible = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )

            mockMvc().perform(
                MockMvcRequestBuilders.get("/users/1")
                    .header("user-id", LoginStatus.ANONYMOUS.id)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk)
        }

    }

    @Nested
    inner class ProfileImageTest {

        @Test
        @DisplayName("[200] 프로필 이미지 업로드")
        fun uploadProfileImageTest() {
            val mockFile = MockMultipartFile(
                "file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".toByteArray()
            )

            doNothing().`when`(userService).uploadProfileImage(1, mockFile)

            mockMvc().perform(
                MockMvcRequestBuilders
                    .multipart("/users/me/profile-image")
                    .file(mockFile)
                    .header("user-id", 1L)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            )
                .andExpect(status().isOk)
        }

        @Test
        @DisplayName("[200] 프로필 이미지 삭제")
        fun deleteProfileImageTest() {
            doNothing().`when`(userService).deleteProfileImage(1)

            mockMvc().perform(
                MockMvcRequestBuilders
                    .delete("/users/me/profile-image")
                    .header("user-id", 1L)
            )
                .andExpect(status().isOk)
        }
    }

    override fun getController() = UserController(userService)
}

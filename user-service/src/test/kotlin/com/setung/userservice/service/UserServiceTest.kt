package com.setung.userservice.service

import com.setung.userservice.config.TestContainerConfig
import com.setung.userservice.dto.LoginRequest
import com.setung.userservice.dto.UserSignupRequest
import com.setung.userservice.entity.EmailCodeType
import com.setung.userservice.error.DuplicationException
import com.setung.userservice.error.InvalidEmailCodeException
import com.setung.userservice.error.InvalidPasswordException
import com.setung.userservice.error.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import


@SpringBootTest
@Import(TestContainerConfig::class)
class UserServiceTest @Autowired constructor(
    val emailCodeService: EmailCodeService,
    val userService: UserService
) {

    @Nested
    inner class UserSignupTest {

        @Test
        @DisplayName("회원가입 성공 테스트")
        fun signupSuccess() {
            val email = "signup_success_test@gmail.com"
            val code = emailCodeService.generateEmailCode(email, EmailCodeType.SIGNUP)
            val userId = userService.signup(UserSignupRequest(email, "name", "password", code))

            val user = userService.findById(userId)
            assertThat(user.email).isEqualTo(email)
        }

        @Test
        @DisplayName("잘못된 인증 코드로 회원가입 실패")
        fun signupFailsWithInvalidVerificationCode() {
            val email = "signup_failure_test_with_wrong_email_Code@gmail.com"
            emailCodeService.generateEmailCode(email, EmailCodeType.SIGNUP)

            assertThrows<InvalidEmailCodeException> {
                userService.signup(UserSignupRequest(email, "name", "password", "wrong"))
            }
        }

        @Test
        @DisplayName("인증 코드 발급 없이 회원가입 실패")
        fun signupFailsWhenNoVerificationCodeIssued() {
            val email = "signup_failure_test_without_email_Code@gmail.com"
            assertThrows<NotFoundException> {
                userService.signup(UserSignupRequest(email, "name", "password", "wrong"))
            }
        }

        @Test
        @DisplayName("중복 이메일로 회원가입 실패")
        fun signupFailsWithDuplicateEmail() {
            val email = "signup_duplicated_email@test.com"
            val code = emailCodeService.generateEmailCode(email, EmailCodeType.SIGNUP)
            userService.signup(UserSignupRequest(email, "name", "password", code))

            assertThrows<DuplicationException> {
                userService.signup(UserSignupRequest(email, "name", "password", code))
            }
        }
    }

    @Nested
    inner class LoginTest {

        @Test
        @DisplayName("로그인 성공 테스트")
        fun loginSuccessTest() {
            val token = userService.login(LoginRequest("tester_1@test.com", "1234"))
            assertThat(token).isNotNull()
        }

        @Test
        @DisplayName("로그인 실패 테스트 - 가입하지 않은 이메일")
        fun loginFailureTestWithNoJoinedEmail() {
            assertThrows<NotFoundException> {
                userService.login(LoginRequest("not_joined_Email@test.com", "1234"))
            }
        }

        @Test
        @DisplayName("로그인 실패 테스트 - 비밀번호 틀림")
        fun loginFailureTestWithWrongPassword() {
            assertThrows<InvalidPasswordException> {
                userService.login(LoginRequest("tester_1@test.com", "12345"))
            }
        }

    }

}
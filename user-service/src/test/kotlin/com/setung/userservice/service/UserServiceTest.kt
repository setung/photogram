package com.setung.userservice.service

import com.setung.userservice.config.TestContainerConfig
import com.setung.userservice.dto.*
import com.setung.userservice.entity.EmailCodeType
import com.setung.userservice.entity.UserStatus
import com.setung.userservice.error.DuplicationException
import com.setung.userservice.error.InvalidEmailCodeException
import com.setung.userservice.error.InvalidPasswordException
import com.setung.userservice.error.NotFoundException
import com.setung.userservice.repo.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertTrue


@SpringBootTest
@Import(TestContainerConfig::class)
class UserServiceTest @Autowired constructor(
    val emailCodeService: EmailCodeService,
    val userService: UserService,
    val userRepository: UserRepository
) {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

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

    @Nested
    inner class UpdateTest {

        @Test
        @DisplayName("유저 업데이트 성공 테스트")
        fun userUpdateSuccessTest() {
            val user = userService.findById(2)
            val request = UserUpdateRequest("updatedName", "updatedBiography", true)

            userService.update(user.id!!, request)
            val updatedUser = userService.findById(2)

            assertThat(updatedUser.name).isEqualTo(request.name)
            assertThat(updatedUser.biography).isEqualTo(request.biography)
            assertThat(updatedUser.isPrivate).isEqualTo(request.isPrivate)
        }

        @Test
        @DisplayName("패스워드 업데이트 성공 테스트")
        fun passwordUpdateSuccessTest() {
            val user = userService.findById(2)
            val code = emailCodeService.generateEmailCode(user.email, EmailCodeType.PASSWORD_RESET)
            val request = PasswordUpdateRequest("12341234", code)

            userService.updatePassword(user.id!!, request)
            val updatedUser = userService.findById(2)

            assertTrue(passwordEncoder.matches(request.password, updatedUser.password))
        }

        @Test
        @DisplayName("패스워드 업데이트 실패 테스트 - emailCode가 유효하지않은 경우")
        fun passwordUpdateFailureTestWitInvalidEmailCode() {
            val user = userService.findById(2)
            emailCodeService.generateEmailCode(user.email, EmailCodeType.PASSWORD_RESET)
            val request = PasswordUpdateRequest("12341234", "invalid-code")

            assertThrows<InvalidEmailCodeException> { userService.updatePassword(user.id!!, request) }
        }
    }

    @Nested
    inner class DeleteTest {

        @Test
        @DisplayName("삭제 성공 테스트 - 삭제 후 상태는 DELETE가 되고 삭제된 이메일로 재가입이 가능")
        fun deleteSuccessTest() {
            val user = userService.findById(3)
            val deleteCode = emailCodeService.generateEmailCode(user.email, EmailCodeType.ACCOUNT_DELETE)

            userService.delete(user.id!!, UserDeleteRequest(deleteCode))
            val deletedUser = userRepository.findById(3).get()

            assertThat(deletedUser.status).isEqualTo(UserStatus.DELETED)

            val signupCode = emailCodeService.generateEmailCode(user.email, EmailCodeType.SIGNUP)
            val newUserId = userService.signup(UserSignupRequest(user.email, user.name, user.password, signupCode))

            val newUser = userService.findById(newUserId)

            assertThat(newUser.email).isEqualTo(user.email)
            assertThat(newUser.status).isEqualTo(UserStatus.NORMAL)
        }

        @Test
        @DisplayName("조회 실패 테스트 - 삭제된 계정은 조회시 예외")
        fun findDeletedUserFailureTest() {
            assertThrows<NotFoundException> { userService.findById(4) }
        }
    }

    @Nested
    inner class FindTest {

        @Test
        @DisplayName("자신 조회 - public")
        fun findMePublicTest() {
            val user = userService.findMe(5)
            assertThat(user.email).isNotNull()
        }

        @Test
        @DisplayName("자신 조회 - private 상태도 정상으로 조회 가능")
        fun findMePrivateTest() {
            val user = userService.findMe(6)
            assertThat(user.email).isNotNull()
        }

        @Test
        @DisplayName("유저 조회 - public")
        fun findUserPublicTest() {
            val user = userService.findUser(5)
            assertThat(user.email).isNotNull()
        }

        @Test
        @DisplayName("유저 조회 - private")
        fun findUserPrivateTest() {
            val user = userService.findUser(6)
            assertThat(user.email).isNull()
        }

    }
}

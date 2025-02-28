package com.setung.service

import com.setung.config.TestContainerConfig
import com.setung.entity.EmailCodeType
import com.setung.error.InvalidEmailCodeException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import kotlin.test.Test

@SpringBootTest
@Import(TestContainerConfig::class)
class EmailCodeServiceTest @Autowired constructor(
    val emailCodeService: EmailCodeService,
) {

    @Test
    @DisplayName("인증코드 발행 성공 테스트")
    fun generateEmailCodeSuccess() {
        val email = "emailcode_generate_test@test.com"
        val generateEmailCode = emailCodeService.generateEmailCode(email, EmailCodeType.SIGNUP)

        assertDoesNotThrow {
            emailCodeService.verifyEmailCode(email, generateEmailCode, EmailCodeType.SIGNUP)
        }

        assertThat(generateEmailCode).isNotNull()
        assertThat(generateEmailCode).hasSize(6)
    }

    @Test
    @DisplayName("잘못된 인증 코드로 인증 실패")
    fun verifyEmailCodeFailsWithWrongCode() {
        val email = "emailcode_verify_test_with_wrong_code@test.com"
        emailCodeService.generateEmailCode(email, EmailCodeType.SIGNUP)
        assertThrows<InvalidEmailCodeException> {
            emailCodeService.verifyEmailCode(email, "wrong_code", EmailCodeType.SIGNUP)
        }
    }
}
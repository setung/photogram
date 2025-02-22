package com.setung.auth.jwt

import io.jsonwebtoken.security.SignatureException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class JwtProviderTest {

    private val jwtProvider = JwtProvider("mySuperSecretKeyForJwtTokenMySuperSecretKey", 1000L)

    @Test
    @DisplayName("토큰 생성 후 검증 및 유저 ID 추출이 정상적으로 동작해야 한다")
    fun shouldCreateAndValidateTokenSuccessfully() {
        val userId = 123L
        val token = jwtProvider.createToken(userId)

        assertNotNull(token)
        assertTrue(jwtProvider.validateToken(token))
        assertEquals(userId, jwtProvider.getUserId(token))
    }

    @Test
    @DisplayName("만료된 토큰은 검증에 실패해야 한다")
    fun shouldFailForExpiredToken() {
        val expiredJwtProvider = JwtProvider("mySuperSecretKeyForJwtTokenMySuperSecretKey", -1000L)
        val token = expiredJwtProvider.createToken(123L)

        assertFalse(expiredJwtProvider.validateToken(token))
    }

    @Test
    @DisplayName("다른 서명으로 생성된 토큰은 예외가 발생해야 한다")
    fun shouldThrowExceptionForInvalidSignature() {
        val userId = 123L
        val token = jwtProvider.createToken(userId)

        val anotherJwtProvider = JwtProvider("DifferentSecretKeyForJwtDifferentKey", 1000L)

        assertFalse(anotherJwtProvider.validateToken(token))
        assertThrows(SignatureException::class.java) {
            anotherJwtProvider.getUserId(token)
        }
    }
}

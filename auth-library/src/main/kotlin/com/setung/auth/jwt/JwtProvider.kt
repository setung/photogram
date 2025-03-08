package com.setung.auth.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
@ConditionalOnProperty(prefix = "jwt", name = ["enabled"], havingValue = "true", matchIfMissing = false)
class JwtProvider(
    @Value(("\${jwt.secret}"))
    private val secretKey: String,

    @Value(("\${jwt.expiration}"))
    private val validityInMilliseconds: Long,
) {

    private val key: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun createToken(userId: Long): String {
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(Date())
            .expiration(validity)
            .signWith(key)
            .compact()
    }

    fun getUserId(token: String): Long {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
            .toLong()
    }

    fun validateToken(token: String): Boolean {
        return try {
            val payload = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload

            if (payload.expiration.before(Date())) return false

            true
        } catch (e: Exception) {
            false
        }
    }

}
package com.setung.userservice.client

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

interface EmailClient {

    fun send(email: String, message: String)
}

@Component
@Profile("default", "local")
class MockEmailClient : EmailClient {

    private val logger = LoggerFactory.getLogger(MockEmailClient::class.java)

    override fun send(email: String, message: String) {
        logger.info("[MOCK] 이메일 전송: $email -> $message")
    }
}
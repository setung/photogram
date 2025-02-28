package com.setung.file

import com.setung.mail.MockEmailClient
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Component
interface FileClient {

    fun upload(file: MultipartFile): String

    fun delete(url: String)
}

@Component
@Profile("default", "local")
class MockFileClient : FileClient {

    private val logger = LoggerFactory.getLogger(MockEmailClient::class.java)

    override fun upload(file: MultipartFile) =
        "https://mock-storage.com/${UUID.randomUUID()}_${file.originalFilename}"

    override fun delete(url: String) {
        logger.info("[MOCK] delete file: $url")
    }
}

package com.setung.error

import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.stereotype.Component
import kotlin.text.Charsets.UTF_8

@Component
class FeignErrorDecoder : ErrorDecoder {

    override fun decode(methodKey: String?, response: Response?): Exception {
        val message = response?.body()?.asInputStream()?.bufferedReader(UTF_8)?.use { it.readText() } ?: "Unknown Error"

        when (response!!.status()) {
            404 -> throw NotFoundException(message)
            else -> throw BadRequestException(message)
        }
    }

}
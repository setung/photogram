package com.setung.auth.resolver

import com.setung.auth.annotation.LoginUser
import com.setung.auth.constant.HttpHeader
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.nio.charset.StandardCharsets

@Component
class LoginUserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.getParameterAnnotation(LoginUser::class.java) != null &&
                (parameter.parameterType == Long::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.nativeRequest as HttpServletRequest
        val userId = request.getHeader(HttpHeader.USER_ID.value) ?: throw HttpClientErrorException.create(
            HttpStatus.UNAUTHORIZED, "Unauthorized access", HttpHeaders(), byteArrayOf(), StandardCharsets.UTF_8
        )
        return userId.toLong()
    }
}
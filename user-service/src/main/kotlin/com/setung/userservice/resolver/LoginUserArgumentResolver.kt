package com.setung.userservice.resolver

import com.setung.auth.constant.HttpHeader
import com.setung.userservice.annotation.LoginUser
import com.setung.userservice.error.UnAuthorizationException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

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
    ): Any? {
        val request = webRequest.nativeRequest as HttpServletRequest
        val userId = request.getHeader(HttpHeader.USER_ID.value) ?: throw UnAuthorizationException("Invalid User")
        return userId.toLong()
    }
}
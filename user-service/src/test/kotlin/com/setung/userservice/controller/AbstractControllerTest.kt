package com.setung.userservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.setung.userservice.error.GlobalExceptionHandler
import com.setung.userservice.resolver.LoginUserArgumentResolver
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

abstract class AbstractControllerTest {

    var objectMapper: ObjectMapper = ObjectMapper()

    abstract fun getController(): Any

    fun mockMvc() = MockMvcBuilders.standaloneSetup(getController())
        .setControllerAdvice(GlobalExceptionHandler())
        .setCustomArgumentResolvers(LoginUserArgumentResolver())
        .alwaysDo<StandaloneMockMvcBuilder?>(MockMvcResultHandlers.print())
        .build()
}
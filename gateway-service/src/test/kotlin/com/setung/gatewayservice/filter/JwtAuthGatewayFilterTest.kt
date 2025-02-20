package com.setung.gatewayservice.filter

import com.setung.auth.jwt.JwtProvider
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.http.server.reactive.MockServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.adapter.DefaultServerWebExchange
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver
import org.springframework.web.server.session.DefaultWebSessionManager
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class JwtAuthGatewayFilterTest {

    private val jwtProvider: JwtProvider = Mockito.mock(JwtProvider::class.java)
    private val filter: JwtAuthGatewayFilter = JwtAuthGatewayFilter(jwtProvider)
    private val chain: GatewayFilterChain = Mockito.mock(GatewayFilterChain::class.java)

    private fun createExchange(request: MockServerHttpRequest): ServerWebExchange {
        return DefaultServerWebExchange(
            request,
            MockServerHttpResponse(),
            DefaultWebSessionManager(),
            ServerCodecConfigurer.create(),
            AcceptHeaderLocaleContextResolver()
        )
    }

    @Test
    @DisplayName("Authorization 헤더가 없는 경우 401 반환")
    fun shouldReturn401WhenAuthorizationHeaderIsMissing() {
        val request = MockServerHttpRequest.get("/test").build()
        val exchange = createExchange(request)

        val filterInstance = filter.apply(JwtAuthGatewayFilter.Config())
        val result = filterInstance.filter(exchange, chain)

        StepVerifier.create(result)
            .expectComplete()
            .verify()

        assert(exchange.response.statusCode == HttpStatus.UNAUTHORIZED)
    }

    @Test
    @DisplayName("잘못된 JWT 토큰이 전달된 경우 401 반환")
    fun shouldReturn401WhenInvalidJwtTokenIsProvided() {
        val request = MockServerHttpRequest.get("/test")
            .header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken")
            .build()
        val exchange = createExchange(request)

        Mockito.`when`(jwtProvider.validateToken("invalidToken")).thenReturn(false)

        val filterInstance = filter.apply(JwtAuthGatewayFilter.Config())
        val result = filterInstance.filter(exchange, chain)

        StepVerifier.create(result)
            .expectComplete()
            .verify()

        assert(exchange.response.statusCode == HttpStatus.UNAUTHORIZED)
    }

    @Test
    @DisplayName("유효한 JWT 토큰이 전달되면 User-Id 헤더 추가 후 요청 전달")
    fun shouldAddUserIdHeaderAndPassRequestWhenValidJwtTokenIsProvided() {
        val validToken = "validToken"
        val userId = 123L

        val request = MockServerHttpRequest.get("/test")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $validToken")
            .build()
        val exchange = createExchange(request)

        Mockito.`when`(jwtProvider.validateToken(validToken)).thenReturn(true)
        Mockito.`when`(jwtProvider.getUserId(validToken)).thenReturn(userId)

        Mockito.`when`(chain.filter(Mockito.any())).thenAnswer { invocation ->
            val mutatedExchange = invocation.getArgument<ServerWebExchange>(0)
            assert(mutatedExchange.request.headers.containsKey("User-Id"))
            assert(mutatedExchange.request.headers["User-Id"]!!.first() == userId.toString())
            Mono.empty<Void>()
        }

        val filterInstance = filter.apply(JwtAuthGatewayFilter.Config())
        val result = filterInstance.filter(exchange, chain)

        StepVerifier.create(result)
            .expectComplete()
            .verify()
    }

}

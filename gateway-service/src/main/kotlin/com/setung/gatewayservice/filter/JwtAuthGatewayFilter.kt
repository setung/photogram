package com.setung.gatewayservice.filter

import com.setung.auth.jwt.JwtProvider
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthGatewayFilter(
    private val jwtProvider: JwtProvider
) : AbstractGatewayFilterFactory<JwtAuthGatewayFilter.Config>(Config::class.java) {

    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request

            if (!request.headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                return@GatewayFilter onError(exchange, "No authorization header")
            }

            val authorizationHeader = request.headers[HttpHeaders.AUTHORIZATION]!![0]
            val jwt = authorizationHeader.replace("Bearer ", "")

            if (!jwtProvider.validateToken(jwt)) {
                return@GatewayFilter onError(exchange, "JWT token is not valid")
            }

            val newRequest = exchange.request.mutate()
                .headers {
                    it.remove(HttpHeaders.AUTHORIZATION)
                    it.set("User-Id", jwtProvider.getUserId(jwt).toString())
                }
                .build()

            return@GatewayFilter chain.filter(exchange.mutate().request(newRequest).build())
        }
    }

    private fun onError(exchange: ServerWebExchange, err: String): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        val buffer = response.bufferFactory().wrap(err.toByteArray(Charsets.UTF_8))

        return response.writeWith(Mono.just(buffer))
    }

    class Config
}

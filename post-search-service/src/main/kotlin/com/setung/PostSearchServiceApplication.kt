package com.setung

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
class PostSearchServiceApplication

fun main(args: Array<String>) {
    runApplication<PostSearchServiceApplication>(*args)
}
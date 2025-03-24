package com.setung.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient("post-service")
@Component
interface PostServiceClient {

    @GetMapping("/posts/ids")
    fun findAllIdsByWriterId(
        @RequestParam writerId: Long,
        @RequestParam pageSize: Int
    ): List<Long>


}
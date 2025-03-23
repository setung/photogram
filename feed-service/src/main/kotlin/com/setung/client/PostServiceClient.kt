package com.setung.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component

@FeignClient("post-service")
@Component
interface PostServiceClient {
}
package com.setung.controller

import com.setung.service.PostDocService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PostSearchController(
    private val postSearchService: PostDocService,
) {

    @GetMapping("/posts/search")
    fun searchByTag(
        @RequestParam tag: String,
        @RequestParam(required = false, defaultValue = Long.MAX_VALUE.toString()) lastPostId: Long,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ) = ResponseEntity(postSearchService.searchByTag(tag, lastPostId, size), HttpStatus.OK)

}

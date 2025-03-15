package com.setung.controller

import com.setung.auth.annotation.LoginUser
import com.setung.dto.PostUploadRequest
import com.setung.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService
) {

    @PostMapping
    fun upload(
        @LoginUser loginUserId: Long,
        @RequestPart("request") request: PostUploadRequest,
        @RequestPart("images") images: List<MultipartFile>
    ) =
        ResponseEntity(postService.upload(loginUserId, request, images), HttpStatus.OK)

}


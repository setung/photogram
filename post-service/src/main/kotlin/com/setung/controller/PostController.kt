package com.setung.controller

import com.setung.auth.annotation.LoginUser
import com.setung.dto.PostUpdateRequest
import com.setung.dto.PostUploadRequest
import com.setung.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
        @RequestPart("images", required = false) images: List<MultipartFile> = emptyList()
    ) =
        ResponseEntity(postService.upload(loginUserId, request, images), HttpStatus.OK)

    @DeleteMapping("/{postId}")
    fun delete(@LoginUser loginUserId: Long, @PathVariable postId: Long) =
        ResponseEntity(postService.delete(loginUserId, postId), HttpStatus.OK)

    @PostMapping("/{postId}")
    fun update(
        @LoginUser loginUserId: Long,
        @PathVariable postId: Long,
        @RequestPart("request") request: PostUpdateRequest,
        @RequestPart("newImages", required = false) newImages: List<MultipartFile> = emptyList()
    ) =
        ResponseEntity(postService.update(loginUserId, postId, request, newImages), HttpStatus.OK)

    @GetMapping("/{postId}")
    fun findById(@LoginUser loginUserId: Long, @PathVariable postId: Long) =
        ResponseEntity(postService.findPost(loginUserId, postId), HttpStatus.OK)

    @GetMapping()
    fun findAllByUserId(
        @LoginUser loginUserId: Long,
        @RequestParam(required = true) userId: Long,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "10") size: Int
    ) {

    }
}


package com.setung.controller

import com.setung.auth.annotation.LoginUser
import com.setung.service.FollowService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FollowController(
    private val followService: FollowService
) {

    @PostMapping("users/{followingId}/follow")
    fun follow(@LoginUser loginUserId: Long, @PathVariable followingId: Long) =
        ResponseEntity(followService.follow(loginUserId, followingId), HttpStatus.OK)

    @PostMapping("/follows/{followId}/accept")
    fun acceptFollow(@LoginUser loginUserId: Long, @PathVariable followId: Long) =
        ResponseEntity(followService.acceptFollow(loginUserId, followId), HttpStatus.OK)

    @PostMapping("/follows/{followId}/reject")
    fun rejectFollow(@LoginUser loginUserId: Long, @PathVariable followId: Long) =
        ResponseEntity(followService.rejectFollow(loginUserId, followId), HttpStatus.OK)

    @DeleteMapping("/follows/{followId}")
    fun cancelFollow(@LoginUser loginUserId: Long, @PathVariable followId: Long) =
        ResponseEntity(followService.deleteFollow(loginUserId, followId), HttpStatus.OK)

}
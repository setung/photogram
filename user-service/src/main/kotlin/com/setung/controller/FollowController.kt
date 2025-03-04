package com.setung.controller

import com.setung.auth.annotation.LoginUser
import com.setung.service.FollowService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/follows")
class FollowController(
    private val followService: FollowService
) {

    @PostMapping("/{targetUserId}")
    fun follow(@LoginUser loginUserId: Long, @PathVariable targetUserId: Long) =
        ResponseEntity(followService.follow(loginUserId, targetUserId), HttpStatus.OK)

    @PostMapping("/{followId}/accept")
    fun acceptFollow(@LoginUser loginUserId: Long, @PathVariable followId: Long) =
        ResponseEntity(followService.acceptFollow(loginUserId, followId), HttpStatus.OK)

    @PostMapping("/{followId}/reject")
    fun rejectFollow(@LoginUser loginUserId: Long, @PathVariable followId: Long) =
        ResponseEntity(followService.rejectFollow(loginUserId, followId), HttpStatus.OK)

    @DeleteMapping("/{followId}")
    fun cancelFollow(@LoginUser loginUserId: Long, @PathVariable followId: Long) =
        ResponseEntity(followService.deleteFollow(loginUserId, followId), HttpStatus.OK)

}
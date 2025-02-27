package com.setung.userservice.controller

import com.setung.userservice.error.ForbiddenException
import com.setung.userservice.error.SelfFollowException
import com.setung.userservice.service.FollowService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FollowControllerTest : AbstractControllerTest() {

    private val followService: FollowService = Mockito.mock()

    @Test
    @DisplayName("[200] 팔로우 성공 테스트")
    fun followSuccessTest() {
        given(followService.follow(1L, 2L)).willReturn(1)

        mockMvc().perform(
            MockMvcRequestBuilders
                .post("/users/{userId}/follow", 2)
                .header("user-id", 1)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").value(1L))
    }

    @Test
    @DisplayName("[400] 팔로우 실패 테스트 - 셀프 팔로우")
    fun followFailureTestWithSelfFollow() {
        given(followService.follow(1L, 1L)).willThrow(SelfFollowException::class.java)

        mockMvc().perform(
            MockMvcRequestBuilders
                .post("/users/{userId}/follow", 1)
                .header("user-id", 1)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("[200] 팔로우 요청 승인 성공 테스트")
    fun followAcceptSuccessTest() {
        doNothing().`when`(followService).acceptFollow(1, 1)

        mockMvc().perform(
            MockMvcRequestBuilders
                .post("/follows/{followId}/accept", 1)
                .header("user-id", 1)
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("[402] 팔로우 요청 승인 실패 테스트 - 다른 유저의 팔로우 승인")
    fun followAcceptFailureTestWithOthers() {
        given(followService.acceptFollow(1L, 1L)).willThrow(ForbiddenException::class.java)

        mockMvc().perform(
            MockMvcRequestBuilders
                .post("/follows/{followId}/accept", 1)
                .header("user-id", 1)
        )
            .andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("[200] 팔로우 요청 거부 성공 테스트")
    fun followRejectSuccessTest() {
        doNothing().`when`(followService).rejectFollow(1, 1)

        mockMvc().perform(
            MockMvcRequestBuilders
                .post("/follows/{followId}/reject", 1)
                .header("user-id", 1)
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("[200] 팔로우 삭제 성공 테스트")
    fun followDeleteSuccessTest() {
        doNothing().`when`(followService).acceptFollow(1, 1)

        mockMvc().perform(
            MockMvcRequestBuilders
                .delete("/follows/{followId}", 1)
                .header("user-id", 1)
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("[402] 팔로우 삭제 실패 테스트 - 다른 유저의 팔로우 삭제")
    fun followDeleteFailureTestWithOthers() {
        given(followService.deleteFollow(2L, 1L)).willThrow(ForbiddenException::class.java)

        mockMvc().perform(
            MockMvcRequestBuilders
                .delete("/follows/{followId}", 1)
                .header("user-id", 2)
        )
            .andExpect(status().isForbidden)
    }


    override fun getController(): Any = FollowController(followService)
}
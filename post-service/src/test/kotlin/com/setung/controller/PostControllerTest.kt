package com.setung.controller

import com.setung.dto.PostUploadRequest
import com.setung.error.ForbiddenException
import com.setung.service.PostService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PostControllerTest : AbstractControllerTest() {

    private val postService: PostService = Mockito.mock()

    @Test
    @DisplayName("[200] 업로드 성공 테스트")
    fun followSuccessTest() {
        val file = MockMultipartFile("file1", "test-image.jpg1", MediaType.IMAGE_JPEG_VALUE, "test image content".toByteArray())
        val request = PostUploadRequest("contents", mutableListOf("tag1", "tag2", "tag3"))

        given(postService.upload(1, request, mutableListOf(file))).willReturn(1)

        mockMvc().perform(
            MockMvcRequestBuilders
                .multipart("/posts")
                .file("images", file.bytes)
                .file(
                    MockMultipartFile(
                        "request",
                        "request.json",
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsString(request).toByteArray()
                    )
                )
                .header("user-id", "1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("[200] 삭제 성공 테스트")
    fun deletePostSuccessTest() {
        doNothing().`when`(postService).delete(1, 1)

        mockMvc().perform(
            MockMvcRequestBuilders
                .delete("/posts/{postId}", 1)
                .header("user-id", "1")
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("[403] 삭제 실패 테스트 - 다른 유저의 게시글 삭제 요청시 403 반환")
    fun deletePostFailureTestWithOthers() {
        given(postService.delete(1, 1)).willThrow(ForbiddenException::class.java)

        mockMvc().perform(
            MockMvcRequestBuilders
                .delete("/posts/{postId}", 1)
                .header("user-id", "1")
        )
            .andExpect(status().isForbidden)
    }

    override fun getController(): Any = PostController(postService)
}
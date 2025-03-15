package com.setung.controller

import com.setung.dto.*
import com.setung.error.ForbiddenException
import com.setung.error.NotFoundException
import com.setung.service.PostService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

class PostControllerTest : AbstractControllerTest() {

    private val postService: PostService = Mockito.mock()

    @Nested
    inner class UploadTest {

        @Test
        @DisplayName("[200] 업로드 성공 테스트")
        fun uploadSuccessTest() {
            val file = MockMultipartFile("file1", "test-image.jpg1", MediaType.IMAGE_JPEG_VALUE, "test image content".toByteArray())
            val request = PostUploadRequest("contents", mutableSetOf("tag1", "tag2", "tag3"))

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
    }

    @Nested
    inner class DeleteTest {

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
    }

    @Nested
    inner class UpdateTest {
        @Test
        @DisplayName("[200] 게시글 수정 성공 테스트")
        fun updatePostSuccessTest() {
            val request = PostUpdateRequest(
                contents = "update",
                newTags = mutableSetOf("1", "2"),
                deletedPostTagIds = listOf(1, 2),
                deletedImageIds = listOf(1, 2)
            )

            doNothing().`when`(postService).update(1, 1, request, emptyList())

            mockMvc().perform(
                MockMvcRequestBuilders
                    .multipart("/posts/{postId}", 1)
                    .file(
                        MockMultipartFile(
                            "request",
                            "request.json",
                            MediaType.APPLICATION_JSON_VALUE,
                            objectMapper.writeValueAsString(request).toByteArray()
                        )
                    )
                    .header("user-id", "1")
            )
                .andExpect(status().isOk)
        }

        @Test
        @DisplayName("[403] 수정 실패 테스트 - 다른 유저의 게시글 수정 요청시 403 반환")
        fun updatePostFailureTestWithOthers() {
            val request = PostUpdateRequest(
                contents = "update",
                newTags = mutableSetOf("1", "2"),
                deletedPostTagIds = listOf(1, 2),
                deletedImageIds = listOf(1, 2)
            )

            given(postService.update(1, 1, request, emptyList())).willThrow(ForbiddenException::class.java)

            mockMvc().perform(
                MockMvcRequestBuilders
                    .multipart("/posts/{postId}", 1)
                    .file(
                        MockMultipartFile(
                            "request",
                            "request.json",
                            MediaType.APPLICATION_JSON_VALUE,
                            objectMapper.writeValueAsString(request).toByteArray()
                        )
                    )
                    .header("user-id", "1")
            )
                .andExpect(status().isForbidden)
        }
    }

    @Nested
    inner class FindTest {

        @Test
        @DisplayName("[200] 게시글 상세 보기 성공 테스트")
        fun findSuccessTest() {
            given(postService.findPost(1, 1)).willReturn(
                PostDetails(
                    id = 1L,
                    writerId = 1L,
                    contents = "contents",
                    images = listOf(
                        PostImageDetails(1L, "http://image.jpg")
                    ),
                    postTags = listOf(
                        PostTagDetails(1L, "tag1")
                    ),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )

            mockMvc().perform(
                MockMvcRequestBuilders
                    .get("/posts/{postId}", 1)
                    .header("user-id", "1")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.writerId").value(1))
                .andExpect(jsonPath("$.contents").value("contents"))
                .andExpect(jsonPath("$.images[0].id").value(1))
                .andExpect(jsonPath("$.images[0].url").value("http://image.jpg"))
                .andExpect(jsonPath("$.postTags[0].id").value(1))
                .andExpect(jsonPath("$.postTags[0].name").value("tag1"))
        }

        @Test
        @DisplayName("[401] user-id 헤더 없이 요청")
        fun findFailureTestWithoutUserIdHeader() {
            mockMvc().perform(
                MockMvcRequestBuilders
                    .get("/posts/{postId}", 1)
            )
                .andExpect(status().isUnauthorized)
        }

        @Test
        @DisplayName("[404] 존재하지 않는 게시글 요청")
        fun findFailureTestWithNonExistentId() {
            given(postService.findPost(1, -1)).willThrow(NotFoundException::class.java)

            mockMvc().perform(
                MockMvcRequestBuilders
                    .get("/posts/{postId}", -1)
            )
                .andExpect(status().isUnauthorized)
        }
    }

    override fun getController(): Any = PostController(postService)
}
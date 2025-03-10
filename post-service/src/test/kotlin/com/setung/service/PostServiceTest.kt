package com.setung.service

import com.setung.client.UserClient
import com.setung.client.UserDto
import com.setung.dto.PostUpdateRequest
import com.setung.dto.PostUploadRequest
import com.setung.entity.PostStatus
import com.setung.error.ForbiddenException
import com.setung.error.NotFoundException
import com.setung.repo.PostRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
class PostServiceTest {

    @Autowired
    private lateinit var postService: PostService

    @Autowired
    private lateinit var postRepository: PostRepository

    @MockitoBean
    private val userClient: UserClient = Mockito.mock()

    @Nested
    open inner class UploadTest {

        @Test
        @Transactional
        @DisplayName("게시글 업로드 성공 테스트")
        open fun uploadSuccessTest() {
            val request = PostUploadRequest("contents", mutableSetOf("tag1", "tag2", "tag3"))
            val files =
                mutableListOf(
                    MockMultipartFile("file1", "test-image.jpg1", MediaType.IMAGE_JPEG_VALUE, "test image content".toByteArray()),
                    MockMultipartFile("file2", "test-image.jpg2", MediaType.IMAGE_JPEG_VALUE, "test image content".toByteArray())
                )

            val postId = postService.upload(1, request, files)

            val post = postService.findById(postId)
            assertEquals(post.contents, "contents")
            assertEquals(post.writerId, 1)
            assertEquals(post.postTags.size, 3)
            assertEquals(post.images.size, 2)
        }

        @Test
        @Transactional
        @DisplayName("DB에 저장된 태그가 있는 게시물 업로드 시 태그가 재사용된다")
        open fun uploadSuccessTestWithSameTag() {
            val firstPostId = postService.upload(1, PostUploadRequest("contents1", mutableSetOf("tag1")), emptyList())
            val secondPostId = postService.upload(1, PostUploadRequest("contents2", mutableSetOf("tag1")), emptyList())

            val firstPost = postService.findById(firstPostId)
            val secondPost = postService.findById(secondPostId)

            assertEquals(firstPost.postTags[0].tag.id, secondPost.postTags[0].tag.id)
        }

        @Test
        @Transactional
        @DisplayName("동일한 태그명으로 여러개 요청시 한개만 저장된다.")
        open fun uploadSuccessTestWithDupliTags() {
            val postId = postService.upload(1, PostUploadRequest("contents1", mutableSetOf("tag1", "tag1", "tag1")), emptyList())

            val post = postService.findById(postId)

            assertEquals(post.postTags.size, 1)
        }
    }

    @Nested
    inner class DeleteTest {

        @Test
        @DisplayName("삭제 성공 테스트")
        fun deleteSuccessTest() {
            val postId = postService.upload(1, PostUploadRequest("contents1", mutableSetOf()), emptyList())
            postService.delete(1, postId)

            val post = postRepository.findById(postId).get()
            assertEquals(post.status, PostStatus.DELETED)
        }

        @Test
        @DisplayName("삭제 실패 테스트 - 다른 유저의 게시글을 삭제할 수 없다.")
        fun deleteFailuresTest() {
            val postId = postService.upload(1, PostUploadRequest("contents1", mutableSetOf()), emptyList())
            assertThrows<ForbiddenException> { postService.delete(2, postId) }
        }
    }

    @Nested
    open inner class UpdateTest {

        @Test
        @Transactional
        @DisplayName("수정 성공 테스트")
        open fun updateSuccessTest() {
            val uploadRequest = PostUploadRequest("contents", mutableSetOf("tag1", "tag2", "tag3"))
            val files =
                mutableListOf(
                    MockMultipartFile("file1", "test-image.jpg1", MediaType.IMAGE_JPEG_VALUE, "test image content".toByteArray()),
                    MockMultipartFile("file2", "test-image.jpg2", MediaType.IMAGE_JPEG_VALUE, "test image content".toByteArray())
                )

            val postId = postService.upload(1, uploadRequest, files)
            val post = postService.findById(postId)

            val updateRequest = PostUpdateRequest(
                contents = "updated",
                newTags = mutableSetOf("tag1", "tag5", "tag6"),
                deletedImageIds = listOf(post.images[0].id!!),
                deletedPostTagIds = listOf(post.postTags.filter { it.tag.name == "tag2" }[0].id!!)
            )

            postService.update(1, postId, updateRequest, emptyList())

            val updatedPost = postService.findById(postId)
            assertEquals(updatedPost.contents, "updated")
            assertEquals(updatedPost.postTags.size, 4)
            assertEquals(updatedPost.images.size, 1)
        }

        @Test
        @DisplayName("수정 실패 테스트 - 다른 유저의 게시글을 수정할 수 없다.")
        fun updateFailuresTestWithOthers() {
            val postId = postService.upload(1, PostUploadRequest("contents1", mutableSetOf()), emptyList())
            assertThrows<ForbiddenException> {
                postService.update(
                    2, postId,
                    PostUpdateRequest(
                        "contensts", setOf(), listOf(), listOf()
                    ),
                    emptyList()
                )
            }
        }
    }

    @Nested
    open inner class FindTest {

        @Test
        @DisplayName("상세보기 성공 테스트 - 자신의 포스트 조회")
        @Transactional
        open fun findOwnDetailsSuccessTest() {
            val request = PostUploadRequest("contents", mutableSetOf("tag1", "tag2", "tag3"))
            val files =
                mutableListOf(
                    MockMultipartFile("file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, byteArrayOf()),
                )

            val postId = postService.upload(1L, request, files)

            val post = postService.findPost(1L, postId)

            assertEquals(post.id, postId)
            assertEquals(post.writerId, 1L)
            assertNotNull(post.contents)
            assertNotNull(post.postTags)
            assertNotNull(post.images)
        }

        @Test
        @DisplayName("상세보기 성공 테스트 - 공개 유저의 포스트 조회")
        @Transactional
        open fun findPublicDetailsSuccessTest() {
            given(userClient.getUser(2L, 1L)).willReturn(
                UserDto(
                    id = 1L,
                    isVisible = true,
                    email = "tester@test.com",
                    name = "name",
                    biography = "biography",
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )

            val request = PostUploadRequest("contents", mutableSetOf("tag1", "tag2", "tag3"))
            val files =
                mutableListOf(
                    MockMultipartFile("file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, byteArrayOf()),
                )

            val postId = postService.upload(1L, request, files)

            val post = postService.findPost(2L, postId)

            assertEquals(post.id, postId)
            assertEquals(post.writerId, 1L)
            assertNotNull(post.contents)
            assertNotNull(post.postTags)
            assertNotNull(post.images)
        }

        @Test
        @DisplayName("상세보기 성공 테스트 - 비공개 유저 포스트 조회")
        fun findPrivateDetailsSuccessTest() {
            given(userClient.getUser(2L, 1L)).willReturn(
                UserDto(
                    id = 1L,
                    isVisible = false,
                    email = null,
                    name = "name",
                    biography = null,
                    createdAt = null,
                    updatedAt = null,
                )
            )

            val request = PostUploadRequest("contents", mutableSetOf("tag1", "tag2", "tag3"))
            val files =
                mutableListOf(
                    MockMultipartFile("file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, byteArrayOf()),
                )

            val postId = postService.upload(1L, request, files)

            val post = postService.findPost(2L, postId)

            assertEquals(post.id, postId)
            assertEquals(post.writerId, 1L)
            assertNull(post.contents)
            assertNull(post.postTags)
            assertNull(post.images)
        }

        @Test
        @DisplayName("상세보기 실패 테스트 - 존재하지 않는 id")
        fun findFailureTestWithNonExistentId() {
            assertThrows<NotFoundException> { postService.findById(-1) }
        }
    }
}
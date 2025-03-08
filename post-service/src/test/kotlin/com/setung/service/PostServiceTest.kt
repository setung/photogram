package com.setung.service

import com.setung.dto.PostUploadRequest
import com.setung.entity.PostStatus
import com.setung.error.ForbiddenException
import com.setung.repo.PostRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@SpringBootTest
class PostServiceTest {

    @Autowired
    private lateinit var postService: PostService

    @Autowired
    private lateinit var postRepository: PostRepository

    @Nested
    open inner class UploadTest {

        @Test
        @Transactional
        @DisplayName("게시글 업로드 성공 테스트")
        open fun uploadSuccessTest() {
            val request = PostUploadRequest("contents", mutableListOf("tag1", "tag2", "tag3"))
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
            val firstPostId = postService.upload(1, PostUploadRequest("contents1", mutableListOf("tag1")), emptyList())
            val secondPostId = postService.upload(1, PostUploadRequest("contents2", mutableListOf("tag1")), emptyList())

            val firstPost = postService.findById(firstPostId)
            val secondPost = postService.findById(secondPostId)

            assertEquals(firstPost.postTags[0].tag.id, secondPost.postTags[0].tag.id)
        }
    }

    @Nested
    inner class DeleteTest {

        @Test
        @DisplayName("삭제 성공 테스트")
        fun deleteSuccessTest() {
            val postId = postService.upload(1, PostUploadRequest("contents1", mutableListOf()), emptyList())
            postService.delete(1, postId)

            val post = postRepository.findById(postId).get()
            assertEquals(post.status, PostStatus.DELETED)
        }

        @Test
        @DisplayName("삭제 실패 테스트 - 다른 유저의 게시글을 삭제할 수 없다.")
        fun deleteFailuresTest() {
            val postId = postService.upload(1, PostUploadRequest("contents1", mutableListOf()), emptyList())
            assertThrows<ForbiddenException> { postService.delete(2, postId) }
        }
    }

}
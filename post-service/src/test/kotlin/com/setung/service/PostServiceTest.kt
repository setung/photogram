package com.setung.service

import com.setung.client.UserClient
import com.setung.client.UserDto
import com.setung.dto.CommentAddRequest
import com.setung.dto.PostUpdateRequest
import com.setung.dto.PostUploadRequest
import com.setung.entity.PostStatus
import com.setung.error.ForbiddenException
import com.setung.error.NotFoundException
import com.setung.repo.CommentRepository
import com.setung.repo.PostRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Transactional
import kotlin.test.*

@SpringBootTest
class PostServiceTest {

    @Autowired
    private lateinit var commentRepository: CommentRepository

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
            given(userClient.getUser(2L, 1L)).willReturn(getUser(isVisible = true))

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
            given(userClient.getUser(2L, 1L)).willReturn(getUser(isVisible = false))

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

        @Test
        @DisplayName("목록 조회 성공 테스트 - 공개 유저")
        fun findAllSuccessTestWithVisibleUser() {
            given(userClient.getUser(1L, 3L)).willReturn(getUser(isVisible = true))

            val postsWithoutLastPostId = postService.findAllByWriterId(1, 3, null, 10)
            val postsWithLastPostId = postService.findAllByWriterId(1, 3, 3, 10)

            assertEquals(postsWithoutLastPostId.size, 3)
            assertEquals(postsWithLastPostId.size, 2)
        }

        @Test
        @DisplayName("목록 조회 성공 테스트 - 비공개 유저")
        fun findAllSuccessTestWithNonVisibleUser() {
            given(userClient.getUser(-1, 4)).willReturn(getUser(isVisible = false))

            val posts = postService.findAllByWriterId(-1, 4, null, 10)

            assertTrue(posts.isEmpty())
        }

        @Test
        @DisplayName("목록 조회 실패 테스트 - 존재하지 않는 유저 id")
        fun findAllSuccessTestWithNonExistedUser() {
            given(userClient.getUser(1L, -1L)).willThrow(NotFoundException(""))

            assertThrows<NotFoundException> { postService.findAllByWriterId(1, -1, null, 10) }
        }

    }

    @Nested
    open inner class CommentTest {

        @Test
        @Transactional
        @DisplayName("댓글 등록 성공 테스트")
        open fun addCommentSuccessTest() {
            given(userClient.getUser(1L, 4L)).willReturn(getUser(isVisible = true))

            postService.addComment(1L, 7L, CommentAddRequest("hello"))

            val post = postService.findById(7)

            assertFalse(post.comments.isEmpty())
        }

        @Test
        @DisplayName("댓글 등록 실패 테스트 - 팔로우가 아닌 private 유저 게시글엔 등록 불가")
        fun addCommentFailureTestWithPrivateUserPost() {
            given(userClient.getUser(1L, 4L)).willReturn(getUser(isVisible = false))
            assertThrows<ForbiddenException> { postService.addComment(1L, 7L, CommentAddRequest("hello")) }
        }

        @Test
        @DisplayName("댓글 등록 실패 테스트 - 존재하지 않은 게시글에 댓글 등록 불가")
        fun addCommentFailureTestWithNonExistentPost() {
            given(userClient.getUser(1L, 4L)).willReturn(getUser(isVisible = false))
            assertThrows<NotFoundException> { postService.addComment(1L, -1L, CommentAddRequest("hello")) }
        }

        @Test
        @Transactional
        @DisplayName("댓글 삭제 성공 테스트 - 자기가 작성한 댓글 삭제")
        open fun deleteCommentSuccessTest() {
            given(userClient.getUser(1L, 4L)).willReturn(getUser(isVisible = true))
            val commentId = postService.addComment(1L, 7L, CommentAddRequest("hello"))

            postService.deleteComment(1L, commentId)
        }

        @Test
        @Transactional
        @DisplayName("댓글 삭제 성공 테스트 - 게시글 작성자는 모든 댓글 삭제 가능")
        open fun deleteCommentSuccessTestWithPostWriter() {
            given(userClient.getUser(1L, 4L)).willReturn(getUser(isVisible = true))
            val commentId = postService.addComment(1L, 7L, CommentAddRequest("hello"))

            postService.deleteComment(4L, commentId)
        }


        @Test
        @Transactional
        @DisplayName("댓글 수정 성공 테스트")
        open fun updateCommentSuccessTest() {
            given(userClient.getUser(1L, 4L)).willReturn(getUser(isVisible = true))
            val commentId = postService.addComment(1L, 7L, CommentAddRequest("hello"))

            postService.updateComment(1L, commentId, CommentAddRequest("world"))

            val comment = commentRepository.findById(commentId).get()
            assertEquals(comment.content, "world")
        }

        @Test
        @Transactional
        @DisplayName("댓글 수정 성공 테스트 - 자신의 댓글이 아니라면 예외")
        open fun updateCommentFailureTest() {
            given(userClient.getUser(1L, 4L)).willReturn(getUser(isVisible = true))
            val commentId = postService.addComment(1L, 7L, CommentAddRequest("hello"))

            assertThrows<ForbiddenException> { postService.updateComment(4L, commentId, CommentAddRequest("world")) }
        }

    }

    private fun getUser(isVisible: Boolean) = UserDto(
        id = 1L,
        isVisible = isVisible,
        email = null,
        name = "name",
        biography = null,
        createdAt = null,
        updatedAt = null,
    )
}
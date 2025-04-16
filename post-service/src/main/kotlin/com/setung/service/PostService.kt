package com.setung.service

import com.setung.client.UserClient
import com.setung.dto.*
import com.setung.entity.CommentEntity
import com.setung.entity.PostEntity
import com.setung.entity.PostStatus
import com.setung.entity.TagEntity
import com.setung.error.ForbiddenException
import com.setung.error.NotFoundException
import com.setung.file.FileClient
import com.setung.producer.PostEventProducer
import com.setung.repo.CommentRepository
import com.setung.repo.PostRepository
import com.setung.repo.TagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class PostService(
    private val postRepository: PostRepository,
    private val fileClient: FileClient,
    private val tagRepository: TagRepository,
    private val userClient: UserClient,
    private val postEventProducer: PostEventProducer,
    private val commentRepository: CommentRepository,
) {

    @Transactional
    fun upload(loginUserId: Long, request: PostUploadRequest, images: List<MultipartFile>): Long {
        val fileUrls = fileClient.upload(images)
        val tags = request.tags.map {
            tagRepository.findByName(it) ?: tagRepository.save(TagEntity.of(it))
        }

        val post = postRepository.save(PostEntity.of(loginUserId, request, fileUrls, tags))

        postEventProducer.sendPostUploadEvent(post)

        return post.id!!
    }

    fun findById(postId: Long) =
        postRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
            ?: throw NotFoundException("Could not find post with id $postId")

    @Transactional
    fun delete(loginUserId: Long, postId: Long) {
        val post = findById(postId)

        if (post.writerId != loginUserId)
            throw ForbiddenException("Could not delete another user's post. id: $postId")

        post.delete()
        postEventProducer.sendPostDeleteEvent(post)
    }

    @Transactional
    fun update(loginUserId: Long, postId: Long, request: PostUpdateRequest, images: List<MultipartFile>) {
        val post = findById(postId)

        if (post.writerId != loginUserId)
            throw ForbiddenException("Could not delete another user's post. id: $postId")

        val fileUrls = fileClient.upload(images)
        val tags = request.newTags.map {
            tagRepository.findByName(it) ?: tagRepository.save(TagEntity.of(it))
        }

        postEventProducer.sendPostUpdateEvent(post)
        post.update(request, fileUrls, tags)
    }

    fun findPost(loginUserId: Long, postId: Long): PostDetails {
        val post = findById(postId)

        if (loginUserId == post.writerId)
            return PostDetails.ofPublic(post)

        val writer = userClient.getUser(loginUserId, post.writerId)
        if (writer.isVisible) {
            return PostDetails.ofPublic(post)
        }

        return PostDetails.ofPrivate(post)
    }

    fun findAllByWriterId(loginUserId: Long, writerId: Long, lastPostId: Long?, pageSize: Int): List<PostSummary> {
        val writer = userClient.getUser(loginUserId, writerId)

        if (writer.isVisible) {
            val postIds = if (lastPostId == null) postRepository.findAllIdsByWriterId(writerId, pageSize)
            else postRepository.findAllIdsByWriterId(writerId, lastPostId, pageSize)

            return postRepository.findAllByIds(postIds).map { PostSummary.of(it) }
        }

        return emptyList()
    }

    fun findAllIdsByWriterId(writerId: Long, pageSize: Int): List<Long> {
        return postRepository.findAllIdsByWriterId(writerId, pageSize)
    }

    fun findAllByIds(postIds: List<Long>) = postRepository.findAllByIds(postIds).map { PostDetails.ofPublic(it) }

    fun addComment(loginUserId: Long, postId: Long, request: CommentAddRequest): Long {
        val post = findById(postId)
        val loginUser = userClient.getUser(loginUserId, post.writerId)

        if (loginUser.isVisible) {
            return commentRepository.save(CommentEntity.of(loginUserId, request, post)).id!!
        } else
            throw ForbiddenException("Could not add comment: $postId")
    }

    fun deleteComment(loginUserId: Long, commentId: Long) {
        val comment = commentRepository.findById(commentId).orElseThrow { NotFoundException("Could not find comment with id: $commentId") }

        if (comment.writerId != loginUserId && comment.post.writerId != loginUserId)
            throw ForbiddenException("Could not delete comment: $commentId")

        commentRepository.deleteById(commentId)
    }

    fun getComments(loginUserId: Long, postId: Long): List<CommentDetails> {
        val post = findById(postId)
        val loginUser = userClient.getUser(loginUserId, post.writerId)

        return if (loginUser.isVisible) {
            post.comments.map {
                CommentDetails.from(it)
            }
        } else
            emptyList()
    }

    @Transactional
    fun updateComment(loginUserId: Long, commentId: Long, request: CommentAddRequest) {
        val comment = commentRepository.findById(commentId).orElseThrow { NotFoundException("Could not find comment: $commentId") }

        if (comment.writerId != loginUserId)
            throw ForbiddenException("Could not update comment: $commentId")

        comment.update(request)
    }
}
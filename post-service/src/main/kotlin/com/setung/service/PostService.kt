package com.setung.service

import com.setung.dto.PostUpdateRequest
import com.setung.dto.PostUploadRequest
import com.setung.entity.PostEntity
import com.setung.entity.PostStatus
import com.setung.entity.TagEntity
import com.setung.error.ForbiddenException
import com.setung.error.NotFoundException
import com.setung.file.FileClient
import com.setung.repo.PostRepository
import com.setung.repo.TagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class PostService(
    private val postRepository: PostRepository,
    private val fileClient: FileClient,
    private val tagRepository: TagRepository
) {

    @Transactional
    fun upload(loginUserId: Long, request: PostUploadRequest, images: List<MultipartFile>): Long {
        val fileUrls = fileClient.upload(images)
        val tags = request.tags.map {
            tagRepository.findByName(it) ?: tagRepository.save(TagEntity.of(it))
        }

        return postRepository.save(PostEntity.of(loginUserId, request, fileUrls, tags)).id!!
    }

    fun findById(postId: Long) =
        postRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
            ?: throw NotFoundException("Could not find post with id $postId")

    fun delete(loginUserId: Long, postId: Long) {
        val post = findById(postId)

        if (post.writerId != loginUserId)
            throw ForbiddenException("Could not delete another user's post. id: $postId")

        post.delete()
        postRepository.save(post)
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

        post.update(request, fileUrls, tags)
    }
}
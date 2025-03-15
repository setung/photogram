package com.setung.service

import com.setung.dto.PostUploadRequest
import com.setung.entity.PostEntity
import com.setung.entity.TagEntity
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
        postRepository.findById(postId).orElseThrow { NotFoundException("Could not find post with id ${postId}") }

}
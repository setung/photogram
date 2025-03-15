package com.setung.dto

import com.setung.entity.PostEntity
import java.time.LocalDateTime

data class PostDetails(
    val id: Long,
    val writerId: Long,
    val contents: String?,
    val images: List<PostImageDetails>?,
    val postTags: List<PostTagDetails>?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {

        fun ofPublic(post: PostEntity) = PostDetails(
            id = post.id!!,
            writerId = post.writerId,
            contents = post.contents,
            images = post.images.map { PostImageDetails(it.id!!, it.url) },
            postTags = post.postTags.map { PostTagDetails(it.id!!, it.tag.name) },
            createdAt = post.createdAt,
            updatedAt = post.updatedAt
        )

        fun ofPrivate(post: PostEntity) = PostDetails(
            id = post.id!!,
            writerId = post.writerId,
            contents = null,
            images = null,
            postTags = null,
            createdAt = null,
            updatedAt = null
        )

    }
}
package com.setung.dto

import com.setung.entity.PostEntity

data class PostSummary(
    val postId: Long,
    val firstImage: PostImageDetails?,
) {

    companion object {
        fun of(post: PostEntity) = PostSummary(
            postId = post.id!!,
            firstImage = if (post.images.isNotEmpty()) {
                PostImageDetails(post.images.first().id!!, post.images.first().url)
            } else {
                null
            }
        )
    }

}

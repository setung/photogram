package com.setung.dto

import com.setung.entity.CommentEntity
import java.time.LocalDateTime

data class CommentDetails(
    val id: Long,
    val postId: Long,
    val writerId: Long,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {

    companion object {
        fun from(comment: CommentEntity) = CommentDetails(
            id = comment.id!!,
            postId = comment.post.id!!,
            writerId = comment.writerId,
            content = comment.content,
            createdAt = comment.createdAt,
            updatedAt = comment.updatedAt,
        )
    }
}

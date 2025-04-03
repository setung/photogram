package com.setung.entity

import com.setung.dto.CommentAddRequest
import jakarta.persistence.*

@Entity
@Table(name = "comment")
class CommentEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var content: String,

    val writerId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    val post: PostEntity

) : BaseEntity() {
    fun update(request: CommentAddRequest) {
        content = request.content
    }

    companion object {
        fun of(whiterId: Long, request: CommentAddRequest, post: PostEntity) = CommentEntity(
            content = request.content,
            writerId = whiterId,
            post = post
        )
    }
}
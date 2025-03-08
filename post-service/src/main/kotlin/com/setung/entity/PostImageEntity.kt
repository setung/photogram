package com.setung.entity

import jakarta.persistence.*

@Entity
@Table(name = "post_image")
class PostImageEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    val post: PostEntity,

    val url: String

) : BaseEntity() {

    companion object {
        fun of(post: PostEntity, url: String) = PostImageEntity(
            post = post,
            url = url
        )
    }
}
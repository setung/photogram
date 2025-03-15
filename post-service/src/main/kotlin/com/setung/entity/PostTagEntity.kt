package com.setung.entity

import jakarta.persistence.*

@Entity
@Table(name = "post_tag")
class PostTagEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    val post: PostEntity,

    @ManyToOne
    val tag: TagEntity
) {

    companion object {
        fun of(post: PostEntity, tag: TagEntity) = PostTagEntity(
            post = post,
            tag = tag
        )
    }

}
package com.setung.entity

import com.setung.dto.CommentAddRequest
import com.setung.dto.PostUpdateRequest
import com.setung.dto.PostUploadRequest
import jakarta.persistence.*

@Entity
@Table(name = "post")
class PostEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val writerId: Long,

    @Enumerated(EnumType.STRING)
    var status: PostStatus,

    var contents: String,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    var images: MutableList<PostImageEntity> = mutableListOf(),

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    var postTags: MutableList<PostTagEntity> = mutableListOf(),

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<CommentEntity> = mutableListOf()

) : BaseEntity() {

    fun delete() {
        status = PostStatus.DELETED
    }

    fun update(request: PostUpdateRequest, newImages: List<String>, newTags: List<TagEntity>) {
        contents = request.contents

        images.removeIf { it.id in request.deletedImageIds }
        images.addAll(newImages.map { PostImageEntity.of(this, it) })

        val existingTagNames = postTags.map { it.tag.name }.toSet()
        val uniqueTags = newTags.filter { it.name !in existingTagNames }
        postTags.removeIf { it.id in request.deletedPostTagIds }
        postTags.addAll(uniqueTags.map { PostTagEntity.of(this, it) })
    }

    fun addComment(loginUser: Long, request: CommentAddRequest) {
        comments.add(CommentEntity(content = request.content, writerId = loginUser, post = this))
    }

    companion object {
        fun of(writerId: Long, request: PostUploadRequest, images: List<String>, tags: List<TagEntity>): PostEntity {
            val post = PostEntity(
                writerId = writerId,
                contents = request.contents,
                status = PostStatus.NORMAL
            )

            post.images.addAll(images.map { PostImageEntity.of(post, it) })
            post.postTags.addAll(tags.map { PostTagEntity.of(post, it) })

            return post
        }
    }
}
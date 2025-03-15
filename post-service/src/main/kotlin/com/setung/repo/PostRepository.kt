package com.setung.repo

import com.setung.entity.PostEntity
import com.setung.entity.PostStatus
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<PostEntity, Long> {

    fun findByIdAndStatus(id: Long, status: PostStatus): PostEntity?
}
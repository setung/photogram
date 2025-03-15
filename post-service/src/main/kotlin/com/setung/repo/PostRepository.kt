package com.setung.repo

import com.setung.entity.PostEntity
import com.setung.entity.PostStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostRepository : JpaRepository<PostEntity, Long> {

    fun findByIdAndStatus(id: Long, status: PostStatus): PostEntity?

    @Query(
        value = """
            SELECT p.id
            FROM PostEntity p
            WHERE p.writerId = :writerId AND p.id < :lastPostId AND p.status = "NORMAL"
            ORDER BY p.id DESC 
            LIMIT :limit
        """
    )
    fun findAllIdsByWriterId(writerId: Long, lastPostId: Long, limit: Int): List<Long>

    @Query(
        value = """
            SELECT p.id
            FROM PostEntity p
            WHERE p.writerId = :writerId AND p.status = "NORMAL"
            ORDER BY p.id DESC 
            LIMIT :limit
        """
    )
    fun findAllIdsByWriterId(writerId: Long, limit: Int): List<Long>

    @Query(
        value = """
            SELECT p
            FROM PostEntity p
            LEFT JOIN FETCH p.images
            WHERE p.id IN :ids
            ORDER BY p.id DESC 
        """
    )
    fun findAllByIds(ids: List<Long>): List<PostEntity>
}
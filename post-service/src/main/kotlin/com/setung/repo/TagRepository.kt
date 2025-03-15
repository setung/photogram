package com.setung.repo

import com.setung.entity.TagEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<TagEntity, Long> {

    fun findByName(name: String): TagEntity?
}
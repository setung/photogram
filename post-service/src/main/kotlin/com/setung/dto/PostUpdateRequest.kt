package com.setung.dto

data class PostUpdateRequest(
    val contents: String,
    val newTags: Set<String>,
    val deletedPostTagIds: List<Long>,
    val deletedImageIds: List<Long>,
)

package com.setung.dto

data class PostUploadRequest(
    val contents: String,
    val tags: MutableList<String>
)
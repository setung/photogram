package com.setung.repo

import co.elastic.clients.elasticsearch.core.SearchResponse
import com.setung.document.PostDocument

interface PostDocCustomRepository {
    fun searchByTags(keyword: String, lastPostId: Long, size: Int): SearchResponse<PostDocument>
}
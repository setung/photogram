package com.setung.repo

import com.setung.document.PostDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository


interface PostDocRepository : ElasticsearchRepository<PostDocument, String>, PostDocCustomRepository {

    fun findByWriterId(writerId: String): List<PostDocument>
}
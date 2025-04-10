package com.setung.config

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest
import jakarta.annotation.PostConstruct
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class ElasticsearchIndexCreator(
    private val elasticsearchClient: ElasticsearchClient,
) {
    
    @PostConstruct
    fun createIndex() {
        val indexName = "photogram_posts"
        val resource = ClassPathResource("elasticsearch/migrations/index_photogram_posts.json")

        val exists = elasticsearchClient.indices().exists { it.index(indexName) }.value()
        if (exists) return

        val request = CreateIndexRequest.Builder()
            .index(indexName)
            .withJson(resource.inputStream)
            .build()

        elasticsearchClient.indices().create(request)
    }
}

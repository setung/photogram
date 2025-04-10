package com.setung.repo

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.SearchResponse
import com.setung.document.PostDocument


class PostDocCustomRepositoryImpl(
    private val elasticsearchClient: ElasticsearchClient
) : PostDocCustomRepository {

    override fun searchByTags(keyword: String, lastPostId: Long, size: Int): SearchResponse<PostDocument> {
        val exactMatch = TermQuery.Builder()
            .field("tags.keyword")
            .value(keyword)
            .boost(3.0f)
            .build()._toQuery()

        val fuzzyMatch = MatchQuery.Builder()
            .field("tags.fuzzy")
            .query(keyword)
            .fuzziness("AUTO")
            .build()._toQuery()

        val ngramMatch = MatchQuery.Builder()
            .field("tags")
            .query(keyword)
            .build()._toQuery()

        val visibilityFilter = TermQuery.Builder()
            .field("isVisible")
            .value(true)
            .build()._toQuery()

        val boolQuery = BoolQuery.Builder()
            .should(listOf(exactMatch, fuzzyMatch, ngramMatch))
            .minimumShouldMatch("1")
            .filter(listOf(visibilityFilter))
            .build()._toQuery()

        val request = SearchRequest.Builder()
            .index("photogram_posts")
            .query(boolQuery)
            .size(size)
            .sort { s ->
                s.field { f -> f.field("id").order(SortOrder.Desc) }
            }
            .searchAfter(lastPostId)
            .build()

        val result = elasticsearchClient.search(request, PostDocument::class.java)
        result.hits().hits()

        return elasticsearchClient.search(request, PostDocument::class.java)
    }
}
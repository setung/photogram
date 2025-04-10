package com.setung.document

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.setung.client.PostDetails
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "photogram_posts")
class PostDocument(

    @Id
    @Field(type = FieldType.Keyword)
    @JsonProperty("id")
    val id: String,

    @Field(type = FieldType.Keyword)
    @JsonProperty("thumbnailUrl")
    var thumbnailUrl: String?,

    @Field(type = FieldType.Boolean)
    @JsonProperty("isVisible")
    var isVisible: Boolean,

    @Field(type = FieldType.Keyword)
    @JsonProperty("writerId")
    val writerId: String,

    @Field(type = FieldType.Text)
    @JsonProperty("tags")
    var tags: List<String>?,
) {

    fun update(post: PostDetails) {
        thumbnailUrl = post.images?.first()?.url
        tags = post.postTags?.map { it.name }!!
    }

}
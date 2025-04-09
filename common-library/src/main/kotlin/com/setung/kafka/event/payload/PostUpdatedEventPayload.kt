package com.setung.kafka.event.payload

import com.fasterxml.jackson.annotation.JsonProperty
import com.setung.kafka.event.EventPayload

data class PostUpdatedEventPayload(
    @JsonProperty("postId") val postId: Long,
    @JsonProperty("writerId") val writerId: Long,
) : EventPayload {
}
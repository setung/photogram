package com.setung.kafka.event.payload

import com.fasterxml.jackson.annotation.JsonProperty
import com.setung.kafka.event.EventPayload

data class UserUnfollowedEventPayload(
    @JsonProperty("userId") val userId: Long,
    @JsonProperty("postIds") val postIds: List<Long>
) : EventPayload {
}
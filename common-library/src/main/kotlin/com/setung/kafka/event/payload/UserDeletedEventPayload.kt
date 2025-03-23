package com.setung.kafka.event.payload

import com.fasterxml.jackson.annotation.JsonProperty
import com.setung.kafka.event.EventPayload

data class UserDeletedEventPayload(
    @JsonProperty("deletedUserId") val deletedUserId: Long,
    @JsonProperty("followerIds") val followerIds: List<Long>,
    @JsonProperty("postIds") val postIds: List<Long>
) : EventPayload {
}
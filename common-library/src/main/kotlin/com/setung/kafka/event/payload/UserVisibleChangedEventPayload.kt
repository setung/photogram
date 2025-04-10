package com.setung.kafka.event.payload

import com.fasterxml.jackson.annotation.JsonProperty
import com.setung.kafka.event.EventPayload

class UserVisibleChangedEventPayload(
    @JsonProperty("userId") val userId: Long,
    @JsonProperty("visible") val isVisible: Boolean
) : EventPayload {
}
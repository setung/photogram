package com.setung.consumer.eventhandler

import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.UserVisibleChangedEventPayload
import com.setung.service.PostDocService
import org.springframework.stereotype.Component

@Component
class UserVisibleChangedEventHandler(
    private val postDocService: PostDocService
) : EventHandler<UserVisibleChangedEventPayload> {

    override fun handle(event: Event<UserVisibleChangedEventPayload>) {
        val userId = event.payload.userId
        val isVisible = event.payload.isVisible
        postDocService.changeUserStatus(userId.toString(), isVisible)
    }
}
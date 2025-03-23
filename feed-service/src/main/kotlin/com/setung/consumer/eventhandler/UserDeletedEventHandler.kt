package com.setung.consumer.eventhandler

import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.UserDeletedEventPayload
import com.setung.repo.FeedRepository
import org.springframework.stereotype.Component

@Component
class UserDeletedEventHandler(
    private val feedRepository: FeedRepository
) : EventHandler<UserDeletedEventPayload> {

    override fun handle(event: Event<UserDeletedEventPayload>) {
        val payload = event.payload
        feedRepository.removeFeed(payload.userId, payload.postIds)
    }

}
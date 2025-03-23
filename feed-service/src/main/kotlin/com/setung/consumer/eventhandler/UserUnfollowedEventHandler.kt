package com.setung.consumer.eventhandler

import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.UserUnfollowedEventPayload
import com.setung.repo.FeedRepository
import org.springframework.stereotype.Component

@Component
class UserUnfollowedEventHandler(
    private val feedRepository: FeedRepository
) : EventHandler<UserUnfollowedEventPayload> {

    override fun handle(event: Event<UserUnfollowedEventPayload>) {
        val payload = event.payload
        feedRepository.removeFeed(payload.userId, payload.postIds)
    }

}
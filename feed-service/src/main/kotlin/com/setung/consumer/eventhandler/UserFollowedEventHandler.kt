package com.setung.consumer.eventhandler

import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.UserFollowedEventPayload
import com.setung.repo.FeedRepository
import org.springframework.stereotype.Component

@Component
class UserFollowedEventHandler(
    private val feedRepository: FeedRepository
) : EventHandler<UserFollowedEventPayload> {

    override fun handle(event: Event<UserFollowedEventPayload>) {
        val payload = event.payload
        feedRepository.addFeed(payload.postIds, payload.userId)
    }

}
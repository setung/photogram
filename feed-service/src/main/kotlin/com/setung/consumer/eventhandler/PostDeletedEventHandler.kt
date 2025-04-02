package com.setung.consumer.eventhandler

import com.setung.client.UserServiceClient
import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.PostDeletedEventPayload
import com.setung.repo.FeedRepository
import org.springframework.stereotype.Component

@Component
class PostDeletedEventHandler(
    private val feedRepository: FeedRepository,
    private val userServiceClient: UserServiceClient
) : EventHandler<PostDeletedEventPayload> {

    override fun handle(event: Event<PostDeletedEventPayload>) {
        val payload = event.payload
        val followers = userServiceClient.getUserFollowers(payload.writerId)
        feedRepository.removeFeed(followers, listOf(payload.postId))
    }

}
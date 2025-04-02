package com.setung.consumer.eventhandler

import com.setung.client.UserServiceClient
import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.PostUploadedEventPayload
import com.setung.repo.FeedRepository
import org.springframework.stereotype.Component

@Component
class PostUploadedEventHandler(
    private val feedRepository: FeedRepository,
    private val userServiceClient: UserServiceClient
) : EventHandler<PostUploadedEventPayload> {

    override fun handle(event: Event<PostUploadedEventPayload>) {
        val payload = event.payload
        val followers = userServiceClient.getUserFollowers(payload.writerId)
        feedRepository.addFeed(payload.postId, followers)
    }

}
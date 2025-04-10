package com.setung.consumer.eventhandler

import com.setung.client.PostServiceClient
import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.PostUpdatedEventPayload
import com.setung.service.PostDocService
import org.springframework.stereotype.Component

@Component
class PostUpdatedEventHandler(
    private val postDocService: PostDocService,
    private val postServiceClient: PostServiceClient
) : EventHandler<PostUpdatedEventPayload> {

    override fun handle(event: Event<PostUpdatedEventPayload>) {
        val payload = event.payload
        val post = postServiceClient.getPostsByIds(listOf(payload.postId)).first()
        postDocService.update(post)
    }
}
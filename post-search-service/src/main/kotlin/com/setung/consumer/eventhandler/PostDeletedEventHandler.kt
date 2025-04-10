package com.setung.consumer.eventhandler

import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.PostDeletedEventPayload
import com.setung.service.PostDocService
import org.springframework.stereotype.Component

@Component
class PostDeletedEventHandler(
    private val postDocService: PostDocService
) : EventHandler<PostDeletedEventPayload> {

    override fun handle(event: Event<PostDeletedEventPayload>) {
        val postId = event.payload.postId
        postDocService.delete(postId.toString())
    }
}
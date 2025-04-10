package com.setung.consumer.eventhandler

import com.setung.client.PostServiceClient
import com.setung.client.UserServiceClient
import com.setung.document.PostDocument
import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.PostUploadedEventPayload
import com.setung.service.PostDocService
import org.springframework.stereotype.Component

@Component
class PostUploadedEventHandler(
    private val postDocService: PostDocService,
    private val postServiceClient: PostServiceClient,
    private val userServiceClient: UserServiceClient,
) : EventHandler<PostUploadedEventPayload> {

    override fun handle(event: Event<PostUploadedEventPayload>) {
        val payload = event.payload
        val post = postServiceClient.getPostsByIds(listOf(payload.postId)).first()
        val user = userServiceClient.getUser(post.writerId, post.writerId)
        postDocService.save(
            PostDocument(
                id = post.id.toString(),
                thumbnailUrl = post.images?.first()!!.url,
                isVisible = user.isVisible,
                writerId = post.writerId.toString(),
                tags = post.postTags?.map { it.name }
            )
        )
    }
}
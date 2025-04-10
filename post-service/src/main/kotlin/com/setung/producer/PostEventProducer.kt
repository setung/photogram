package com.setung.producer

import com.setung.entity.PostEntity
import com.setung.kafka.event.Event
import com.setung.kafka.event.EventType
import com.setung.kafka.event.payload.PostDeletedEventPayload
import com.setung.kafka.event.payload.PostUpdatedEventPayload
import com.setung.kafka.event.payload.PostUploadedEventPayload
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class PostEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    fun sendPostUploadEvent(post: PostEntity) {
        kafkaTemplate.send(
            EventType.POST_UPLOADED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.POST_UPLOADED,
                payload = PostUploadedEventPayload(
                    postId = post.id!!,
                    writerId = post.writerId
                )
            ).toJson()
        )
    }

    fun sendPostDeleteEvent(post: PostEntity) {
        kafkaTemplate.send(
            EventType.POST_DELETED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.POST_DELETED,
                payload = PostDeletedEventPayload(
                    postId = post.id!!,
                    writerId = post.writerId
                )
            ).toJson()
        )
    }

    fun sendPostUpdateEvent(post: PostEntity) {
        kafkaTemplate.send(
            EventType.POST_UPDATED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.POST_UPDATED,
                payload = PostUpdatedEventPayload(
                    postId = post.id!!,
                    writerId = post.writerId
                )
            ).toJson()
        )
    }
}
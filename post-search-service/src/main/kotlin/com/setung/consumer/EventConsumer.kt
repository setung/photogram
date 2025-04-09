package com.setung.consumer

import com.setung.consumer.eventhandler.PostDeletedEventHandler
import com.setung.consumer.eventhandler.PostUpdatedEventHandler
import com.setung.consumer.eventhandler.PostUploadedEventHandler
import com.setung.consumer.eventhandler.UserVisibleChangedEventHandler
import com.setung.kafka.event.Event
import com.setung.kafka.event.EventPayload
import com.setung.kafka.event.EventTopics
import com.setung.kafka.event.EventType
import com.setung.kafka.event.payload.PostDeletedEventPayload
import com.setung.kafka.event.payload.PostUpdatedEventPayload
import com.setung.kafka.event.payload.PostUploadedEventPayload
import com.setung.kafka.event.payload.UserVisibleChangedEventPayload
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class EventConsumer(
    private val postUploadedEventHandler: PostUploadedEventHandler,
    private val postDeletedEventHandler: PostDeletedEventHandler,
    private val postUpdatedEventHandler: PostUpdatedEventHandler,
    private val userVisibleChangedEventHandler: UserVisibleChangedEventHandler
) {
    @KafkaListener(
        topics = [
            EventTopics.POST_UPLOADED,
            EventTopics.POST_DELETED,
            EventTopics.POST_UPDATED,
            EventTopics.USER_VISIBLE_CHANGED
        ]
    )
    fun listen(message: String, ack: Acknowledgment) {
        val event: Event<EventPayload> = Event.fromJson(message)

        when (event.type) {
            EventType.POST_UPLOADED -> postUploadedEventHandler.handle(event as Event<PostUploadedEventPayload>)
            EventType.POST_DELETED -> postDeletedEventHandler.handle(event as Event<PostDeletedEventPayload>)
            EventType.POST_UPDATED -> postUpdatedEventHandler.handle(event as Event<PostUpdatedEventPayload>)
            EventType.USER_VISIBLE_CHANGED -> userVisibleChangedEventHandler.handle(event as Event<UserVisibleChangedEventPayload>)
            else -> {
                return
            }
        }

        ack.acknowledge()
    }
}

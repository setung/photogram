package com.setung.client

import com.setung.kafka.event.Event
import com.setung.kafka.event.EventPayload
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class EventProducer(
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    fun publish(event: Event<EventPayload>) {
        applicationEventPublisher.publishEvent(event)
    }
}
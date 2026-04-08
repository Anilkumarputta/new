package com.editorial.platform.event.service;

import java.time.Instant;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.editorial.platform.event.KafkaTopics;
import com.editorial.platform.event.model.ContentEvent;
import com.editorial.platform.event.model.ContentEventType;

@Service
public class ContentEventPublisher {

    private final KafkaTemplate<String, ContentEvent> kafkaTemplate;

    public ContentEventPublisher(KafkaTemplate<String, ContentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(
        ContentEventType eventType,
        String entityType,
        Long entityId,
        String title,
        String status,
        String categoryName
    ) {
        ContentEvent event = new ContentEvent();
        event.setEventType(eventType);
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setTitle(title);
        event.setStatus(status);
        event.setCategoryName(categoryName);
        event.setOccurredAt(Instant.now());

        kafkaTemplate.send(KafkaTopics.CONTENT_EVENTS, entityType + "-" + entityId, event);
    }
}

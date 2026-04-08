package com.editorial.platform.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.editorial.platform.event.KafkaTopics;
import com.editorial.platform.event.model.ContentEvent;
import com.editorial.platform.event.model.ContentEventType;
import com.editorial.platform.readmodel.service.ContentReadModelService;

@Component
public class ContentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ContentEventConsumer.class);
    private final ContentReadModelService contentReadModelService;

    public ContentEventConsumer(ContentReadModelService contentReadModelService) {
        this.contentReadModelService = contentReadModelService;
    }

    @KafkaListener(topics = KafkaTopics.CONTENT_EVENTS, groupId = "editorial-content-platform")
    public void consume(ContentEvent event) {
        log.info(
            "Kafka event received: type={}, entityType={}, entityId={}, status={}",
            event.getEventType(),
            event.getEntityType(),
            event.getEntityId(),
            event.getStatus()
        );

        if (event.getEventType() == ContentEventType.SHOW_DELETED
            || event.getEventType() == ContentEventType.WORKOUT_DELETED) {
            contentReadModelService.deleteFromEvent(event);
            return;
        }

        contentReadModelService.upsertFromEvent(event);
    }
}

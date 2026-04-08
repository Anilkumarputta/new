package com.editorial.platform.async.service;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PublishingAsyncService {

    private static final Logger log = LoggerFactory.getLogger(PublishingAsyncService.class);

    @Async("publishingTaskExecutor")
    public CompletableFuture<Void> runPostPublishTasks(String contentType, Long contentId, String title) {
        log.info("Async publishing job started for {} {}", contentType, contentId);

        try {
            // Simulate follow-up work such as notifying downstream systems or refreshing caches.
            Thread.sleep(1200);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.warn("Async publishing job interrupted for {} {}", contentType, contentId);
            return CompletableFuture.completedFuture(null);
        }

        log.info("Async publishing job completed for {} {} ({})", contentType, contentId, title);
        return CompletableFuture.completedFuture(null);
    }
}

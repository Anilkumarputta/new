package com.editorial.platform.readmodel.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.editorial.platform.event.model.ContentEvent;
import com.editorial.platform.readmodel.api.dto.ContentLookupResponse;
import com.editorial.platform.readmodel.model.ContentLookupRow;
import com.editorial.platform.readmodel.repository.ContentLookupRepository;

@Service
public class ContentReadModelService {

    private final ContentLookupRepository contentLookupRepository;

    public ContentReadModelService(ContentLookupRepository contentLookupRepository) {
        this.contentLookupRepository = contentLookupRepository;
    }

    public void upsertFromEvent(ContentEvent event) {
        ContentLookupRow row = new ContentLookupRow();
        row.setKey(new ContentLookupRow.Key(event.getEntityType(), event.getEntityId()));
        row.setTitle(event.getTitle());
        row.setStatus(event.getStatus());
        row.setCategoryName(event.getCategoryName());
        row.setUpdatedAt(event.getOccurredAt() != null ? event.getOccurredAt() : Instant.now());
        contentLookupRepository.save(row);
    }

    public void deleteFromEvent(ContentEvent event) {
        contentLookupRepository.deleteById(new ContentLookupRow.Key(event.getEntityType(), event.getEntityId()));
    }

    public List<ContentLookupResponse> getByContentType(String contentType) {
        return contentLookupRepository.findByKeyContentType(contentType.toUpperCase())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public List<ContentLookupResponse> getAll() {
        return contentLookupRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private ContentLookupResponse toResponse(ContentLookupRow row) {
        ContentLookupResponse response = new ContentLookupResponse();
        response.setContentType(row.getKey().getContentType());
        response.setContentId(row.getKey().getContentId());
        response.setTitle(row.getTitle());
        response.setStatus(row.getStatus());
        response.setCategoryName(row.getCategoryName());
        response.setTrainerName(row.getTrainerName());
        response.setUpdatedAt(row.getUpdatedAt());
        return response;
    }
}

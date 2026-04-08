package com.editorial.platform.readmodel.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.editorial.platform.readmodel.api.dto.ContentLookupResponse;
import com.editorial.platform.readmodel.service.ContentReadModelService;

@RestController
@RequestMapping("/api/v1/read-model/content")
public class ContentReadModelController {

    private final ContentReadModelService contentReadModelService;

    public ContentReadModelController(ContentReadModelService contentReadModelService) {
        this.contentReadModelService = contentReadModelService;
    }

    @GetMapping
    public ResponseEntity<List<ContentLookupResponse>> getContent(@RequestParam(required = false) String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return ResponseEntity.ok(contentReadModelService.getAll());
        }

        return ResponseEntity.ok(contentReadModelService.getByContentType(contentType));
    }
}

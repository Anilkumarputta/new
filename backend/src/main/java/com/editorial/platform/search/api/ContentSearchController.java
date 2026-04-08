package com.editorial.platform.search.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.editorial.platform.search.api.dto.SearchResultResponse;
import com.editorial.platform.search.service.ContentSearchService;

@RestController
@RequestMapping("/api/v1/search")
public class ContentSearchController {

    private final ContentSearchService contentSearchService;

    public ContentSearchController(ContentSearchService contentSearchService) {
        this.contentSearchService = contentSearchService;
    }

    @GetMapping
    public ResponseEntity<List<SearchResultResponse>> search(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String trainer,
        @RequestParam(required = false) String tag
    ) {
        return ResponseEntity.ok(contentSearchService.search(q, category, trainer, tag));
    }
}

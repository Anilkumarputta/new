package com.editorial.platform.show.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.editorial.platform.show.api.dto.ShowRequest;
import com.editorial.platform.show.api.dto.ShowResponse;
import com.editorial.platform.show.service.ShowService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public ResponseEntity<List<ShowResponse>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowResponse> getShowById(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowById(id));
    }

    @PostMapping
    public ResponseEntity<ShowResponse> createShow(@Valid @RequestBody ShowRequest request) {
        ShowResponse createdShow = showService.createShow(request);
        return ResponseEntity.created(URI.create("/api/v1/shows/" + createdShow.getId())).body(createdShow);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShowResponse> updateShow(@PathVariable Long id, @Valid @RequestBody ShowRequest request) {
        return ResponseEntity.ok(showService.updateShow(id, request));
    }

    @PostMapping("/{id}/submit-for-review")
    public ResponseEntity<ShowResponse> submitForReview(@PathVariable Long id) {
        return ResponseEntity.ok(showService.submitForReview(id));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ShowResponse> publish(@PathVariable Long id) {
        return ResponseEntity.ok(showService.publish(id));
    }

    @PostMapping("/{id}/move-back-to-draft")
    public ResponseEntity<ShowResponse> moveBackToDraft(@PathVariable Long id) {
        return ResponseEntity.ok(showService.moveBackToDraft(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id) {
        showService.deleteShow(id);
        return ResponseEntity.noContent().build();
    }
}

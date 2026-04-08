package com.editorial.platform.show.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.editorial.platform.async.service.PublishingAsyncService;
import com.editorial.platform.audit.model.AuditAction;
import com.editorial.platform.audit.model.AuditEntityType;
import com.editorial.platform.audit.service.AuditLogService;
import com.editorial.platform.category.model.Category;
import com.editorial.platform.category.repository.CategoryRepository;
import com.editorial.platform.common.exception.BadRequestException;
import com.editorial.platform.common.exception.ResourceNotFoundException;
import com.editorial.platform.common.model.PublishingStatus;
import com.editorial.platform.event.model.ContentEventType;
import com.editorial.platform.event.service.ContentEventPublisher;
import com.editorial.platform.search.model.SearchDocument;
import com.editorial.platform.search.service.ContentSearchService;
import com.editorial.platform.show.api.dto.ShowRequest;
import com.editorial.platform.show.api.dto.ShowResponse;
import com.editorial.platform.show.model.Show;
import com.editorial.platform.show.repository.ShowRepository;

@Service
@Transactional
public class ShowService {

    private final ShowRepository showRepository;
    private final CategoryRepository categoryRepository;
    private final AuditLogService auditLogService;
    private final ContentEventPublisher contentEventPublisher;
    private final ContentSearchService contentSearchService;
    private final PublishingAsyncService publishingAsyncService;

    public ShowService(
        ShowRepository showRepository,
        CategoryRepository categoryRepository,
        AuditLogService auditLogService,
        ContentEventPublisher contentEventPublisher,
        ContentSearchService contentSearchService,
        PublishingAsyncService publishingAsyncService
    ) {
        this.showRepository = showRepository;
        this.categoryRepository = categoryRepository;
        this.auditLogService = auditLogService;
        this.contentEventPublisher = contentEventPublisher;
        this.contentSearchService = contentSearchService;
        this.publishingAsyncService = publishingAsyncService;
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getAllShows() {
        return showRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public ShowResponse getShowById(Long id) {
        return toResponse(findShow(id));
    }

    public ShowResponse createShow(ShowRequest request) {
        Category category = findCategory(request.getCategoryId());

        Show show = new Show();
        show.setTitle(request.getTitle().trim());
        show.setDescription(request.getDescription().trim());
        show.setCategory(category);
        show.setStatus(PublishingStatus.DRAFT);
        show.setPublished(false);

        Show savedShow = showRepository.save(show);
        auditLogService.logChange(
            AuditEntityType.SHOW,
            savedShow.getId(),
            AuditAction.CREATED,
            null,
            savedShow.getStatus(),
            "Show created"
        );
        publishEvent(savedShow, ContentEventType.SHOW_CREATED);
        indexShow(savedShow);
        return toResponse(savedShow);
    }

    public ShowResponse updateShow(Long id, ShowRequest request) {
        Show show = findShow(id);
        Category category = findCategory(request.getCategoryId());

        show.setTitle(request.getTitle().trim());
        show.setDescription(request.getDescription().trim());
        show.setCategory(category);

        Show savedShow = showRepository.save(show);
        auditLogService.logChange(
            AuditEntityType.SHOW,
            savedShow.getId(),
            AuditAction.UPDATED,
            savedShow.getStatus(),
            savedShow.getStatus(),
            "Show details updated"
        );
        publishEvent(savedShow, ContentEventType.SHOW_UPDATED);
        indexShow(savedShow);
        return toResponse(savedShow);
    }

    public void deleteShow(Long id) {
        Show show = findShow(id);
        showRepository.delete(show);
        auditLogService.logChange(
            AuditEntityType.SHOW,
            id,
            AuditAction.DELETED,
            show.getStatus(),
            null,
            "Show deleted"
        );
        contentEventPublisher.publish(
            ContentEventType.SHOW_DELETED,
            "SHOW",
            id,
            show.getTitle(),
            show.getStatus().name(),
            show.getCategory().getName()
        );
        contentSearchService.delete("SHOW-" + id);
    }

    public ShowResponse submitForReview(Long id) {
        Show show = findShow(id);
        changeStatus(show, PublishingStatus.REVIEW, "Show moved to review");
        Show savedShow = showRepository.save(show);
        publishEvent(savedShow, ContentEventType.SHOW_SENT_TO_REVIEW);
        indexShow(savedShow);
        return toResponse(savedShow);
    }

    public ShowResponse publish(Long id) {
        Show show = findShow(id);
        changeStatus(show, PublishingStatus.PUBLISHED, "Show published");
        show.setPublished(true);
        Show savedShow = showRepository.save(show);
        publishEvent(savedShow, ContentEventType.SHOW_PUBLISHED);
        indexShow(savedShow);
        publishingAsyncService.runPostPublishTasks("SHOW", savedShow.getId(), savedShow.getTitle());
        return toResponse(savedShow);
    }

    public ShowResponse moveBackToDraft(Long id) {
        Show show = findShow(id);
        changeStatus(show, PublishingStatus.DRAFT, "Show moved back to draft");
        show.setPublished(false);
        Show savedShow = showRepository.save(show);
        publishEvent(savedShow, ContentEventType.SHOW_MOVED_TO_DRAFT);
        indexShow(savedShow);
        return toResponse(savedShow);
    }

    private void changeStatus(Show show, PublishingStatus targetStatus, String message) {
        PublishingStatus currentStatus = show.getStatus();

        if (currentStatus == targetStatus) {
            throw new BadRequestException("Show is already in status " + targetStatus.name());
        }

        boolean validTransition =
            (currentStatus == PublishingStatus.DRAFT && targetStatus == PublishingStatus.REVIEW)
                || (currentStatus == PublishingStatus.REVIEW && targetStatus == PublishingStatus.PUBLISHED)
                || (currentStatus == PublishingStatus.PUBLISHED && targetStatus == PublishingStatus.DRAFT);

        if (!validTransition) {
            throw new BadRequestException(
                "Invalid status transition from " + currentStatus.name() + " to " + targetStatus.name()
            );
        }

        show.setStatus(targetStatus);
        auditLogService.logChange(
            AuditEntityType.SHOW,
            show.getId(),
            AuditAction.STATUS_CHANGED,
            currentStatus,
            targetStatus,
            message
        );
    }

    private Show findShow(Long id) {
        return showRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID " + id));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID " + categoryId));
    }

    private ShowResponse toResponse(Show show) {
        ShowResponse response = new ShowResponse();
        response.setId(show.getId());
        response.setTitle(show.getTitle());
        response.setDescription(show.getDescription());
        response.setCategoryId(show.getCategory().getId());
        response.setCategoryName(show.getCategory().getName());
        response.setStatus(show.getStatus().name());
        response.setPublished(show.isPublished());
        response.setCreatedAt(show.getCreatedAt());
        response.setUpdatedAt(show.getUpdatedAt());
        return response;
    }

    private void publishEvent(Show show, ContentEventType eventType) {
        contentEventPublisher.publish(
            eventType,
            "SHOW",
            show.getId(),
            show.getTitle(),
            show.getStatus().name(),
            show.getCategory().getName()
        );
    }

    private void indexShow(Show show) {
        SearchDocument document = new SearchDocument();
        document.setId("SHOW-" + show.getId());
        document.setContentType("SHOW");
        document.setContentId(show.getId());
        document.setTitle(show.getTitle());
        document.setDescription(show.getDescription());
        document.setCategoryName(show.getCategory().getName());
        document.setStatus(show.getStatus().name());
        contentSearchService.index(document);
    }
}

package com.editorial.platform.workout.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.editorial.platform.audit.model.AuditAction;
import com.editorial.platform.audit.model.AuditEntityType;
import com.editorial.platform.audit.service.AuditLogService;
import com.editorial.platform.category.model.Category;
import com.editorial.platform.category.repository.CategoryRepository;
import com.editorial.platform.common.exception.BadRequestException;
import com.editorial.platform.common.exception.ResourceNotFoundException;
import com.editorial.platform.common.model.PublishingStatus;
import com.editorial.platform.workout.api.dto.WorkoutRequest;
import com.editorial.platform.workout.api.dto.WorkoutResponse;
import com.editorial.platform.workout.model.Workout;
import com.editorial.platform.workout.model.WorkoutDifficulty;
import com.editorial.platform.workout.repository.WorkoutRepository;

@Service
@Transactional
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final CategoryRepository categoryRepository;
    private final AuditLogService auditLogService;

    public WorkoutService(
        WorkoutRepository workoutRepository,
        CategoryRepository categoryRepository,
        AuditLogService auditLogService
    ) {
        this.workoutRepository = workoutRepository;
        this.categoryRepository = categoryRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<WorkoutResponse> getAllWorkouts() {
        return workoutRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public WorkoutResponse getWorkoutById(Long id) {
        return toResponse(findWorkout(id));
    }

    public WorkoutResponse createWorkout(WorkoutRequest request) {
        Workout workout = new Workout();
        applyRequest(workout, request);
        workout.setStatus(PublishingStatus.DRAFT);
        Workout savedWorkout = workoutRepository.save(workout);
        auditLogService.logChange(
            AuditEntityType.WORKOUT,
            savedWorkout.getId(),
            AuditAction.CREATED,
            null,
            savedWorkout.getStatus(),
            "Workout created"
        );
        return toResponse(savedWorkout);
    }

    public WorkoutResponse updateWorkout(Long id, WorkoutRequest request) {
        Workout workout = findWorkout(id);
        applyRequest(workout, request);
        Workout savedWorkout = workoutRepository.save(workout);
        auditLogService.logChange(
            AuditEntityType.WORKOUT,
            savedWorkout.getId(),
            AuditAction.UPDATED,
            savedWorkout.getStatus(),
            savedWorkout.getStatus(),
            "Workout details updated"
        );
        return toResponse(savedWorkout);
    }

    public void deleteWorkout(Long id) {
        Workout workout = findWorkout(id);
        workoutRepository.delete(workout);
        auditLogService.logChange(
            AuditEntityType.WORKOUT,
            id,
            AuditAction.DELETED,
            workout.getStatus(),
            null,
            "Workout deleted"
        );
    }

    public WorkoutResponse submitForReview(Long id) {
        Workout workout = findWorkout(id);
        changeStatus(workout, PublishingStatus.REVIEW, "Workout moved to review");
        return toResponse(workoutRepository.save(workout));
    }

    public WorkoutResponse publish(Long id) {
        Workout workout = findWorkout(id);
        changeStatus(workout, PublishingStatus.PUBLISHED, "Workout published");
        return toResponse(workoutRepository.save(workout));
    }

    public WorkoutResponse moveBackToDraft(Long id) {
        Workout workout = findWorkout(id);
        changeStatus(workout, PublishingStatus.DRAFT, "Workout moved back to draft");
        return toResponse(workoutRepository.save(workout));
    }

    private void changeStatus(Workout workout, PublishingStatus targetStatus, String message) {
        PublishingStatus currentStatus = workout.getStatus();

        if (currentStatus == targetStatus) {
            throw new BadRequestException("Workout is already in status " + targetStatus.name());
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

        workout.setStatus(targetStatus);
        auditLogService.logChange(
            AuditEntityType.WORKOUT,
            workout.getId(),
            AuditAction.STATUS_CHANGED,
            currentStatus,
            targetStatus,
            message
        );
    }

    private void applyRequest(Workout workout, WorkoutRequest request) {
        Category category = findCategory(request.getCategoryId());
        WorkoutDifficulty difficulty = parseDifficulty(request.getDifficulty());
        Set<String> cleanedTags = normalizeTags(request.getTags());

        workout.setTitle(request.getTitle().trim());
        workout.setDescription(request.getDescription().trim());
        workout.setTrainerName(request.getTrainerName().trim());
        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setDifficulty(difficulty);
        workout.setCategory(category);
        workout.setTags(cleanedTags);
    }

    private Workout findWorkout(Long id) {
        return workoutRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Workout not found with ID " + id));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID " + categoryId));
    }

    private WorkoutDifficulty parseDifficulty(String difficulty) {
        try {
            return WorkoutDifficulty.valueOf(difficulty.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Difficulty must be BEGINNER, INTERMEDIATE, or ADVANCED");
        }
    }

    private Set<String> normalizeTags(Set<String> tags) {
        Set<String> cleanedTags = tags.stream()
            .map(String::trim)
            .filter(tag -> !tag.isBlank())
            .map(tag -> tag.toLowerCase(Locale.ROOT))
            .collect(LinkedHashSet::new, Set::add, Set::addAll);

        if (cleanedTags.isEmpty()) {
            throw new BadRequestException("At least one non-empty tag is required");
        }

        return cleanedTags;
    }

    private WorkoutResponse toResponse(Workout workout) {
        WorkoutResponse response = new WorkoutResponse();
        response.setId(workout.getId());
        response.setTitle(workout.getTitle());
        response.setDescription(workout.getDescription());
        response.setTrainerName(workout.getTrainerName());
        response.setDurationMinutes(workout.getDurationMinutes());
        response.setDifficulty(workout.getDifficulty().name());
        response.setCategoryId(workout.getCategory().getId());
        response.setCategoryName(workout.getCategory().getName());
        response.setStatus(workout.getStatus().name());
        response.setTags(new LinkedHashSet<>(workout.getTags()));
        response.setCreatedAt(workout.getCreatedAt());
        response.setUpdatedAt(workout.getUpdatedAt());
        return response;
    }
}

package com.editorial.platform.workout.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public WorkoutService(WorkoutRepository workoutRepository, CategoryRepository categoryRepository) {
        this.workoutRepository = workoutRepository;
        this.categoryRepository = categoryRepository;
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
        return toResponse(workoutRepository.save(workout));
    }

    public WorkoutResponse updateWorkout(Long id, WorkoutRequest request) {
        Workout workout = findWorkout(id);
        applyRequest(workout, request);
        return toResponse(workoutRepository.save(workout));
    }

    public void deleteWorkout(Long id) {
        Workout workout = findWorkout(id);
        workoutRepository.delete(workout);
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

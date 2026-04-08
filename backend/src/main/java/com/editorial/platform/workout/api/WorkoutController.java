package com.editorial.platform.workout.api;

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

import com.editorial.platform.workout.api.dto.WorkoutRequest;
import com.editorial.platform.workout.api.dto.WorkoutResponse;
import com.editorial.platform.workout.service.WorkoutService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping
    public ResponseEntity<List<WorkoutResponse>> getAllWorkouts() {
        return ResponseEntity.ok(workoutService.getAllWorkouts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponse> getWorkoutById(@PathVariable Long id) {
        return ResponseEntity.ok(workoutService.getWorkoutById(id));
    }

    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(@Valid @RequestBody WorkoutRequest request) {
        WorkoutResponse createdWorkout = workoutService.createWorkout(request);
        return ResponseEntity.created(URI.create("/api/v1/workouts/" + createdWorkout.getId())).body(createdWorkout);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponse> updateWorkout(@PathVariable Long id, @Valid @RequestBody WorkoutRequest request) {
        return ResponseEntity.ok(workoutService.updateWorkout(id, request));
    }

    @PostMapping("/{id}/submit-for-review")
    public ResponseEntity<WorkoutResponse> submitForReview(@PathVariable Long id) {
        return ResponseEntity.ok(workoutService.submitForReview(id));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<WorkoutResponse> publish(@PathVariable Long id) {
        return ResponseEntity.ok(workoutService.publish(id));
    }

    @PostMapping("/{id}/move-back-to-draft")
    public ResponseEntity<WorkoutResponse> moveBackToDraft(@PathVariable Long id) {
        return ResponseEntity.ok(workoutService.moveBackToDraft(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }
}

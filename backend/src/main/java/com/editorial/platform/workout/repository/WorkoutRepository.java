package com.editorial.platform.workout.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.editorial.platform.workout.model.Workout;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
}

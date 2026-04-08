INSERT INTO categories (name, description) VALUES
('Strength', 'Content focused on strength building and muscle work'),
('Cardio', 'Content focused on endurance and heart health'),
('Recovery', 'Content focused on stretching, mobility, and recovery');

INSERT INTO shows (title, description, category_id, status, published, created_at, updated_at) VALUES
('Morning Burn', 'Fast-paced fitness show for early energy and motivation.', 2, 'DRAFT', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Lift Lab', 'Educational show that explains form, technique, and strength progress.', 1, 'REVIEW', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO workouts (title, description, trainer_name, duration_minutes, difficulty, category_id, status, created_at, updated_at) VALUES
('Power Legs 30', 'Lower body strength workout with controlled sets and rest periods.', 'Ava Brooks', 30, 'INTERMEDIATE', 1, 'DRAFT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Quick Cardio Blast', 'Short workout for raising heart rate and burning calories.', 'Mason Lee', 20, 'BEGINNER', 2, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO workout_tags (workout_id, tag) VALUES
(1, 'legs'),
(1, 'strength'),
(2, 'cardio'),
(2, 'quick');

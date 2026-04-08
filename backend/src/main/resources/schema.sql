DROP TABLE IF EXISTS workout_tags;
DROP TABLE IF EXISTS workouts;
DROP TABLE IF EXISTS shows;
DROP TABLE IF EXISTS categories;

CREATE TABLE categories (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE shows (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(500) NOT NULL,
    category_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_show_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE workouts (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(500) NOT NULL,
    trainer_name VARCHAR(120) NOT NULL,
    duration_minutes INTEGER NOT NULL,
    difficulty VARCHAR(30) NOT NULL,
    category_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_workout_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE workout_tags (
    workout_id BIGINT NOT NULL,
    tag VARCHAR(80) NOT NULL,
    CONSTRAINT fk_workout_tag_workout FOREIGN KEY (workout_id) REFERENCES workouts (id) ON DELETE CASCADE
);

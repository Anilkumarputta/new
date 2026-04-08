package com.editorial.platform.show.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.editorial.platform.show.model.Show;

public interface ShowRepository extends JpaRepository<Show, Long> {
}

package dev.vhcolley.lesson_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.vhcolley.lesson_planner.domain.MappingRun;

public interface MappingRunRepository extends JpaRepository<MappingRun, Long> {
    
}

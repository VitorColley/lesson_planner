package dev.vhcolley.lesson_planner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.vhcolley.lesson_planner.domain.CurriculumDocument;

public interface CurriculumDocumentRepository extends JpaRepository<CurriculumDocument, Long> {
    Optional<CurriculumDocument> findTopByOrderByIdDesc();
    
} 

package dev.vhcolley.lesson_planner.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.vhcolley.lesson_planner.domain.CurriculumChunk;

public interface CurriculumChunkRepository extends JpaRepository<CurriculumChunk, Long> {

    List<CurriculumChunk> findByDocumentIdOrderByChunkIndexAsc(Long documentId, Pageable pageable);

    @Query(value = """
            SELECT *
            FROM curriculum_chunks
            WHERE to_tsvector('english', content) @@ plainto_tsquery('english', :query)
            ORDER BY ts_rank(to_tsvector('english', content), plainto_tsquery('english', :query)) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<CurriculumChunk> searchByContent(@Param("query") String query, @Param("limit") int limit);
    
}

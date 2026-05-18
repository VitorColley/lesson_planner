// References:
// - Spring Data JPA for database interactions and entity management
//https://www.youtube.com/watch?v=cK4mi5kZSoE
//https://spring.io/projects/spring-data-jpa

package dev.vhcolley.lesson_planner.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "mapping_runs", indexes = {
    @Index(name = "idx_mapping_runs_created_at", columnList = "created_at")
})
public class MappingRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="lesson_input", nullable = false, columnDefinition = "text")
    private String lessonInput;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="retrieved_chunk_ids", nullable = false, columnDefinition = "jsonb")
    private List<Long> retrievedChunkIds;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="ai_response_json", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> aiResponseJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (retrievedChunkIds == null) retrievedChunkIds = List.of();
        if (aiResponseJson == null) aiResponseJson = Map.of();
    }

    // getters/setters

    public Long getId() { return id; }

    public String getLessonInput() { return lessonInput; }
    public void setLessonInput(String lessonInput) { this.lessonInput = lessonInput; }

    public List<Long> getRetrievedChunkIds() { return retrievedChunkIds; }
    public void setRetrievedChunkIds(List<Long> retrievedChunkIds) { this.retrievedChunkIds = retrievedChunkIds; }

    public Map<String, Object> getAiResponseJson() { return aiResponseJson; }
    public void setAiResponseJson(Map<String, Object> aiResponseJson) { this.aiResponseJson = aiResponseJson; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
}
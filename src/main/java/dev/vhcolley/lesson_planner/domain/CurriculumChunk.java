// References:
// - Spring Data JPA for database interactions and entity management
// https://www.youtube.com/watch?v=cK4mi5kZSoE
// https://spring.io/projects/spring-data-jpa

package dev.vhcolley.lesson_planner.domain;

import java.time.OffsetDateTime;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;

@Entity
@Table(
    name = "curriculum_chunks",
    uniqueConstraints = @UniqueConstraint(name = "unique_chunk_per_document", columnNames = {"document_id", "chunk_index"}),
        indexes = {
            @Index(name = "idx_document_id", columnList = "document_id"),
            @Index(name = "idx_chunks_strand", columnList = "strand"),
            @Index(name = "idx_chunks_element", columnList = "element"),
            @Index(name ="idx_chunks_learning_outcome_ref", columnList = "learning_outcome_ref")
        }
)

public class CurriculumChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private CurriculumDocument document;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Column(length =120)
    private String strand;

    @Column(length =120)
    private String element;

    @Column(name = "learning_outcome_ref", length =120)
    private String learningOutcomeRef;

    @Column( nullable = false, columnDefinition = "text")
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "source_page_start")
    private Integer sourcePageStart;

    @Column(name = "source_page_end")
    private Integer sourcePageEnd;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void PrePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (metadata == null) {
            metadata = Map.of();
        }
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    public CurriculumDocument getDocument() {
        return document;
    }
    public void setDocument(CurriculumDocument document) {
        this.document = document;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }
    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public String getStrand() {
        return strand;
    }
    public void setStrand(String strand) {
        this.strand = strand;
    }

    public String getElement() {
        return element;
    }
    public void setElement(String element) {
        this.element = element;
    }

    public String getLearningOutcomeRef() {
        return learningOutcomeRef;
    }
    public void setLearningOutcomeRef(String learningOutcomeRef) {
        this.learningOutcomeRef = learningOutcomeRef;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Integer getSourcePageStart() {
        return sourcePageStart;
    }
    public void setSourcePageStart(Integer sourcePageStart) {
        this.sourcePageStart = sourcePageStart;
    }

    public Integer getSourcePageEnd() {
        return sourcePageEnd;
    }
    public void setSourcePageEnd(Integer sourcePageEnd) {
        this.sourcePageEnd = sourcePageEnd;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}

// References:
// - Spring Data JPA for database interactions and entity management
//https://www.youtube.com/watch?v=cK4mi5kZSoE
//https://spring.io/projects/spring-data-jpa

package dev.vhcolley.lesson_planner.domain;

import java.time.OffsetDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "curriculum_documents")
public class CurriculumDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String subject;

    @Column(nullable = false, length = 120)
    private String cycle;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "source_type", nullable = false, length = 40)
    private String sourceType;

    @Column(name = "source_ref", nullable = false, length =500)
    private String sourceRef;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceRef() {
        return sourceRef;
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }    
}

package dev.vhcolley.lesson_planner.dto;

public record MappedOutcome(
        String learningOutcomeRef,
        String curriculumText,
        String justification,
        Long chunkId
) {
}
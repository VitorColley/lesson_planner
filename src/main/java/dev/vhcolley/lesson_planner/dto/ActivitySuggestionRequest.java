package dev.vhcolley.lesson_planner.dto;

import java.util.List;

public record ActivitySuggestionRequest(
        String subject,
        String ageGroup,
        String topic,
        String lessonStage,
        int remainingMinutes,
        List<String> mappedOutcomes,
        List<String> selectedActivityIds,
        String constraints
) {
}
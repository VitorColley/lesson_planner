package dev.vhcolley.lesson_planner.dto;

import java.util.List;

public record LessonState(
        String subject,
        String ageGroup,
        String topic,
        int totalDurationMinutes,
        int remainingMinutes,
        String currentStage,
        List<String> mappedOutcomes,
        List<SelectedActivity> selectedActivities,
        String constraints
) {
}
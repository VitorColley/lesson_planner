package dev.vhcolley.lesson_planner.dto;

import java.util.List;

public record ActivityCard(
        String id,
        String title,
        String method,
        String lessonStage,
        int durationMinutes,
        String description,
        String whyThisFits,
        String teacherRole,
        String studentTask,
        List<String> materials
) {
}
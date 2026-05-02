package dev.vhcolley.lesson_planner.dto;

public record EnhancedActivityText(
        String description,
        String whyThisFits,
        String teacherRole,
        String studentTask
) {
}
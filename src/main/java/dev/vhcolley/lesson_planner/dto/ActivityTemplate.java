package dev.vhcolley.lesson_planner.dto;

import java.util.List;

public record ActivityTemplate(
        String id,
        String title,
        String method,
        String lessonStage,
        int durationMin,
        int durationMax,
        List<String> bestFor,
        List<String> subjects,
        List<String> ageGroups,
        String description,
        String teacherRole,
        String studentTask,
        List<String> materials
) {
}
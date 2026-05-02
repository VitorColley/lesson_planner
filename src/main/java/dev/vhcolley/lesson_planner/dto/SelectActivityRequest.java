package dev.vhcolley.lesson_planner.dto;

public record SelectActivityRequest(
        LessonState lessonState,
        ActivityCard selectedActivity
) {
}
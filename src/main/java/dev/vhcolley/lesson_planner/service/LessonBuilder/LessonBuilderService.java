package dev.vhcolley.lesson_planner.service.LessonBuilder;

import dev.vhcolley.lesson_planner.dto.*;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LessonBuilderService {

    public LessonState applySelection(SelectActivityRequest request) {

        LessonState state = request.lessonState();
        ActivityCard selected = request.selectedActivity();

        // Convert ActivityCard → SelectedActivity
        SelectedActivity activity = new SelectedActivity(
                selected.id(),
                selected.title(),
                selected.method(),
                selected.lessonStage(),
                selected.durationMinutes(),
                selected.description(),
                selected.teacherRole(),
                selected.studentTask(),
                selected.materials()
        );

        // Add to list
        List<SelectedActivity> updatedActivities = new ArrayList<>(state.selectedActivities());
        updatedActivities.add(activity);

        // Update remaining time
        int remaining = state.remainingMinutes() - selected.durationMinutes();
        if (remaining < 0) remaining = 0;

        // Determine next stage
        String nextStage = determineNextStage(state.currentStage());

        return new LessonState(
                state.subject(),
                state.ageGroup(),
                state.topic(),
                state.totalDurationMinutes(),
                remaining,
                nextStage,
                state.mappedOutcomes(),
                updatedActivities,
                state.constraints(),
                0
        );
    }

    private String determineNextStage(String currentStage) {
        return switch (currentStage.toUpperCase()) {
            case "STARTER" -> "MAIN";
            case "MAIN" -> "PRACTICE";
            case "PRACTICE" -> "PLENARY";
            default -> "COMPLETE";
        };
    }
}
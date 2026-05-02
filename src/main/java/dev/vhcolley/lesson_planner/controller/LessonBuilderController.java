package dev.vhcolley.lesson_planner.controller;

import dev.vhcolley.lesson_planner.dto.ActivityCard;
import dev.vhcolley.lesson_planner.dto.LessonState;
import dev.vhcolley.lesson_planner.dto.SelectActivityRequest;
import dev.vhcolley.lesson_planner.service.ActivitySuggestionService;
import dev.vhcolley.lesson_planner.service.LessonBuilderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-builder")
public class LessonBuilderController {

    private final ActivitySuggestionService activitySuggestionService;
    private final LessonBuilderService lessonBuilderService;

    public LessonBuilderController(ActivitySuggestionService activitySuggestionService, LessonBuilderService lessonBuilderService) {
        this.activitySuggestionService = activitySuggestionService;
        this.lessonBuilderService = lessonBuilderService;
    }
    // Endpoint to get activity suggestions based on the lesson context
    @PostMapping("/suggest-activities")
    public ResponseEntity<List<ActivityCard>> suggestActivities(
            @RequestBody LessonState state
    ) {
        List<ActivityCard> suggestions = activitySuggestionService.suggestActivities(state);
        return ResponseEntity.ok(suggestions);
    }

    // Endpoint to select an activity and update the lesson state
    @PostMapping("/select-activity")
    public ResponseEntity<LessonState> selectActivity(
            @RequestBody SelectActivityRequest request
    ) {
        LessonState updatedState = lessonBuilderService.applySelection(request);
        return ResponseEntity.ok(updatedState);
    }
}
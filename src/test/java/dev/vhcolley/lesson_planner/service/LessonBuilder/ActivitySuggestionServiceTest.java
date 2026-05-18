package dev.vhcolley.lesson_planner.service.LessonBuilder;

import dev.vhcolley.lesson_planner.dto.ActivityCard;
import dev.vhcolley.lesson_planner.dto.LessonState;
import dev.vhcolley.lesson_planner.dto.SelectedActivity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivitySuggestionServiceTest {

    @Autowired
    private ActivitySuggestionService activitySuggestionService;

    @Test
    void shouldSuggestStarterActivitiesForScienceJuniorCycle() {
        LessonState state = new LessonState(
                "Science",
                "Junior Cycle",
                "pH scale",
                40,
                40,
                "STARTER",
                List.of("3.6"),
                List.of(),
                "Mixed ability class",
                0
        );

        List<ActivityCard> suggestions = activitySuggestionService.suggestActivities(state);

        assertFalse(suggestions.isEmpty());
        assertTrue(suggestions.size() <= 3);

        assertTrue(suggestions.stream()
                .allMatch(activity -> activity.lessonStage().equalsIgnoreCase("STARTER")));

        assertTrue(suggestions.stream()
                .allMatch(activity -> activity.durationMinutes() <= 40));
    }

    @Test
    void shouldNotSuggestAlreadySelectedActivities() {
        SelectedActivity selected = new SelectedActivity(
                "starter-retrieval-quiz",
                "Retrieval Quiz",
                "Retrieval practice",
                "STARTER",
                5,
                "Students answer recall questions.",
                "Ask questions.",
                "Answer individually.",
                List.of("quiz questions")
        );

        LessonState state = new LessonState(
                "Science",
                "Junior Cycle",
                "pH scale",
                40,
                40,
                "STARTER",
                List.of("3.6"),
                List.of(selected),
                "Mixed ability class",
                0
        );

        List<ActivityCard> suggestions = activitySuggestionService.suggestActivities(state);

        assertTrue(suggestions.stream()
                .noneMatch(activity -> activity.id().equals("starter-retrieval-quiz")));
    }

    @Test
    void shouldReturnEmptyListWhenRemainingTimeIsTooShort() {
        LessonState state = new LessonState(
                "Science",
                "Junior Cycle",
                "pH scale",
                40,
                3,
                "MAIN",
                List.of("3.6"),
                List.of(),
                "Mixed ability class",
                0
        );

        List<ActivityCard> suggestions = activitySuggestionService.suggestActivities(state);

        assertTrue(suggestions.isEmpty());
    }
}
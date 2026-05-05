package dev.vhcolley.lesson_planner.service;

import dev.vhcolley.lesson_planner.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LessonBuilderServiceTest {

    private final LessonBuilderService lessonBuilderService = new LessonBuilderService();

    @Test
    void shouldAddSelectedActivityAndMoveFromStarterToMain() {
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

        ActivityCard selectedCard = new ActivityCard(
                "starter-think-pair-share",
                "Think-Pair-Share",
                "Collaborative learning",
                "STARTER",
                8,
                "Students discuss predictions about acids and bases.",
                "This fits the mapped outcome.",
                "Pose a clear question.",
                "Think, discuss, and share.",
                List.of("prompt question")
        );

        SelectActivityRequest request = new SelectActivityRequest(state, selectedCard);

        LessonState updatedState = lessonBuilderService.applySelection(request);

        assertEquals(32, updatedState.remainingMinutes());
        assertEquals("MAIN", updatedState.currentStage());
        assertEquals(1, updatedState.selectedActivities().size());

        SelectedActivity selectedActivity = updatedState.selectedActivities().get(0);

        assertEquals("starter-think-pair-share", selectedActivity.id());
        assertEquals("Think-Pair-Share", selectedActivity.title());
        assertEquals("STARTER", selectedActivity.lessonStage());
        assertEquals(8, selectedActivity.durationMinutes());
    }

    @Test
    void shouldMoveFromMainToPractice() {
        LessonState state = new LessonState(
                "Science",
                "Junior Cycle",
                "pH scale",
                40,
                32,
                "MAIN",
                List.of("3.6"),
                List.of(),
                "Mixed ability class",
                0
        );

        ActivityCard selectedCard = new ActivityCard(
                "main-guided-investigation",
                "Guided Investigation",
                "Inquiry-based learning",
                "MAIN",
                20,
                "Students investigate pH values.",
                "This supports investigation skills.",
                "Guide the investigation.",
                "Test substances and record results.",
                List.of("pH paper", "samples")
        );

        LessonState updatedState = lessonBuilderService.applySelection(
                new SelectActivityRequest(state, selectedCard)
        );

        assertEquals(12, updatedState.remainingMinutes());
        assertEquals("PRACTICE", updatedState.currentStage());
    }

    @Test
    void shouldNotAllowRemainingTimeToGoBelowZero() {
        LessonState state = new LessonState(
                "Science",
                "Junior Cycle",
                "pH scale",
                40,
                5,
                "PLENARY",
                List.of("3.6"),
                List.of(),
                "Mixed ability class",
                0
        );

        ActivityCard selectedCard = new ActivityCard(
                "plenary-exit-ticket",
                "Exit Ticket",
                "Formative assessment",
                "PLENARY",
                8,
                "Students answer final questions.",
                "This checks understanding.",
                "Collect responses.",
                "Answer the prompt.",
                List.of("exit ticket")
        );

        LessonState updatedState = lessonBuilderService.applySelection(
                new SelectActivityRequest(state, selectedCard)
        );

        assertEquals(0, updatedState.remainingMinutes());
        assertEquals("COMPLETE", updatedState.currentStage());
    }

    @Test
    void shouldPreserveExistingSelectedActivities() {
        SelectedActivity existingActivity = new SelectedActivity(
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
                35,
                "MAIN",
                List.of("3.6"),
                List.of(existingActivity),
                "Mixed ability class",
                0
        );

        ActivityCard selectedCard = new ActivityCard(
                "main-guided-investigation",
                "Guided Investigation",
                "Inquiry-based learning",
                "MAIN",
                20,
                "Students investigate pH values.",
                "This supports investigation skills.",
                "Guide the investigation.",
                "Test substances and record results.",
                List.of("pH paper", "samples")
        );

        LessonState updatedState = lessonBuilderService.applySelection(
                new SelectActivityRequest(state, selectedCard)
        );

        assertEquals(2, updatedState.selectedActivities().size());
        assertEquals("starter-retrieval-quiz", updatedState.selectedActivities().get(0).id());
        assertEquals("main-guided-investigation", updatedState.selectedActivities().get(1).id());
    }
}
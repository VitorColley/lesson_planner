package dev.vhcolley.lesson_planner.service;

import dev.vhcolley.lesson_planner.dto.ActivityCard;
import dev.vhcolley.lesson_planner.dto.LessonState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AiActivityEnhancerServiceTest {

    private final AiActivityEnhancerService enhancerService =
            new AiActivityEnhancerService();

    @Test
    void shouldEnhanceOnlyTextFieldsAndPreserveCardStructure() {
        ActivityCard card = new ActivityCard(
                "starter-think-pair-share",
                "Think-Pair-Share",
                "Collaborative learning",
                "STARTER",
                8,
                "Students discuss a prompt.",
                "This activity supports discussion.",
                "Pose a clear question.",
                "Think and discuss.",
                List.of("prompt question")
        );

        LessonState state = new LessonState(
                "Science",
                "Junior Cycle",
                "pH scale",
                40,
                40,
                "STARTER",
                List.of("3.6"),
                List.of(),
                "Mixed ability class"
        );

        ActivityCard enhanced = enhancerService.enhance(card, state);

        assertEquals(card.id(), enhanced.id());
        assertEquals(card.title(), enhanced.title());
        assertEquals(card.method(), enhanced.method());
        assertEquals(card.lessonStage(), enhanced.lessonStage());
        assertEquals(card.durationMinutes(), enhanced.durationMinutes());
        assertEquals(card.materials(), enhanced.materials());

        assertTrue(enhanced.description().contains("pH scale"));
        assertTrue(enhanced.whyThisFits().contains("3.6"));
        assertTrue(enhanced.teacherRole().contains("pH scale"));
        assertTrue(enhanced.studentTask().contains("pH scale"));
    }
}
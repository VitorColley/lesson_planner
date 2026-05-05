package dev.vhcolley.lesson_planner.service;

import dev.vhcolley.lesson_planner.ai.ActivityEnhancementAgent;
import dev.vhcolley.lesson_planner.dto.ActivityCard;
import dev.vhcolley.lesson_planner.dto.LessonState;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;


class AiActivityEnhancerServiceTest {
    
    private final ActivityEnhancementAgent activityEnhancementAgent = mock(ActivityEnhancementAgent.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AiActivityEnhancerService enhancerService = new AiActivityEnhancerService(activityEnhancementAgent, objectMapper);
    
    

    @Test
    void shouldEnhanceOnlyTextFieldsAndPreserveCardStructure() {
        when(activityEnhancementAgent.enhanceActivity(any(ActivityCard.class), any(LessonState.class)))
                .thenReturn("""
                        {
                          "description": "Students discuss how the pH scale is used to classify acids and bases.",
                          "whyThisFits": "This supports learning outcome 3.6 by connecting discussion to acids, bases and pH.",
                          "teacherRole": "Pose a focused question about pH and guide feedback.",
                          "studentTask": "Think individually, discuss predictions with a partner, and share reasoning."
                        }
                        """);

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
        assertTrue(enhanced.teacherRole().contains("pH"));
        assertTrue(enhanced.studentTask().contains("predictions"));
    }

    @Test
    void shouldReturnOriginalCardWhenAiResponseFails() {
        when(activityEnhancementAgent.enhanceActivity(any(ActivityCard.class), any(LessonState.class))).thenReturn("not valid json");

        ActivityCard card = new ActivityCard(
                "starter-think-pair-share",
                "Think-Pair-Share",
                "Collaborative learning",
                "STARTER",
                8,
                "Original description.",
                "Original fit.",
                "Original teacher role.",
                "Original student task.",
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

        assertEquals(card, enhanced);
    }
}
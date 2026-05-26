package dev.vhcolley.lesson_planner.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import dev.vhcolley.lesson_planner.dto.ActivityCard;
import dev.vhcolley.lesson_planner.dto.LessonState;
import dev.vhcolley.lesson_planner.dto.MappedOutcome;
import dev.vhcolley.lesson_planner.dto.SelectActivityRequest;
import dev.vhcolley.lesson_planner.service.LessonBuilder.ActivitySuggestionService;
import dev.vhcolley.lesson_planner.service.LessonBuilder.LessonBuilderService;

@WebMvcTest(LessonBuilderController.class)
@Import(GlobalExceptionHandler.class)
class LessonBuilderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivitySuggestionService activitySuggestionService;

    @MockitoBean
    private LessonBuilderService lessonBuilderService;

    @SuppressWarnings("null")
@Test
    void shouldReturnActivitySuggestions() throws Exception {
        ActivityCard card = new ActivityCard(
                "starter-think-pair-share",
                "Think-Pair-Share",
                "Collaborative learning",
                "STARTER",
                8,
                "Students think independently, discuss with a partner, then share ideas.",
                "This activity supports learning outcome 3.6.",
                "Pose a clear question and guide feedback.",
                "Think, discuss, and share responses.",
                List.of("prompt question")
        );

        when(activitySuggestionService.suggestActivities(any()))
                .thenReturn(List.of(card));

        String requestBody = """
                {
                "subject": "Science",
                "ageGroup": "Junior Cycle",
                "topic": "pH scale",
                "totalDurationMinutes": 40,
                "remainingMinutes": 40,
                "currentStage": "STARTER",
                "mappedOutcomes": ["3.6"],
                "selectedActivities": [],
                "constraints": "Mixed ability class"
                }
                """;

        mockMvc.perform(post("/api/lesson-builder/suggest-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("starter-think-pair-share"))
                .andExpect(jsonPath("$[0].title").value("Think-Pair-Share"))
                .andExpect(jsonPath("$[0].lessonStage").value("STARTER"))
                .andExpect(jsonPath("$[0].durationMinutes").value(8))
                .andExpect(jsonPath("$[0].materials[0]").value("prompt question"));
    }

    @SuppressWarnings("null")
@Test
    void shouldSelectActivityAndReturnUpdatedLessonState() throws Exception {
        LessonState updatedState = new LessonState(
                "Science",
                "Junior Cycle",
                "pH scale",
                40,
                32,
                "MAIN",
                List.of(
                new MappedOutcome(
                        "3.6",
                        "Students should investigate acids and bases using the pH scale.",
                        "The lesson topic focuses on pH scale.",
                        1L
                )
                ),
                List.of(),
                "Mixed ability class",
                0
        );

        when(lessonBuilderService.applySelection(any(SelectActivityRequest.class)))
                .thenReturn(updatedState);

        String requestBody = """
                {
                  "lessonState": {
                    "subject": "Science",
                    "ageGroup": "Junior Cycle",
                    "topic": "pH scale",
                    "totalDurationMinutes": 40,
                    "remainingMinutes": 40,
                    "currentStage": "STARTER",
                    "mappedOutcomes": ["3.6"],
                    "selectedActivities": [],
                    "constraints": "Mixed ability class"
                  },
                  "selectedActivity": {
                    "id": "starter-think-pair-share",
                    "title": "Think-Pair-Share",
                    "method": "Collaborative learning",
                    "lessonStage": "STARTER",
                    "durationMinutes": 8,
                    "description": "Students think independently, discuss with a partner, then share ideas.",
                    "whyThisFits": "This activity supports learning outcome 3.6.",
                    "teacherRole": "Pose a clear question and guide feedback.",
                    "studentTask": "Think, discuss, and share responses.",
                    "materials": ["prompt question"]
                  }
                }
                """;

        mockMvc.perform(post("/api/lesson-builder/select-activity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("Science"))
                .andExpect(jsonPath("$.remainingMinutes").value(32))
                .andExpect(jsonPath("$.currentStage").value("MAIN"))
                .andExpect(jsonPath("$.mappedOutcomes[0]").value("3.6"));
    }
}
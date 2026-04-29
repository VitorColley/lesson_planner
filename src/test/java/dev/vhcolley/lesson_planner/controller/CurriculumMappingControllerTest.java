package dev.vhcolley.lesson_planner.controller;

import dev.vhcolley.lesson_planner.service.CurriculumMappingService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurriculumController.class)
@Import(GlobalExceptionHandler.class)
class CurriculumMappingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurriculumMappingService mappingService;

    @Test
    void shouldMapCurriculumAndReturnMappedOutcomes() throws Exception {
        Map<String, Object> serviceResponse = Map.of(
                "result", Map.of(
                        "mappedOutcomes", List.of(
                                Map.of(
                                        "chunk_id", 10,
                                        "learning_outcome_ref", "3.6",
                                        "justification", "The lesson links to acids, bases and the pH scale."
                                )
                        ),
                        "notes", "The curriculum context was sufficient."
                ),
                "retrievedChunksIds", List.of(1, 2, 3, 4, 5)
        );

        when(mappingService.mapLessonToCurriculum(contains("Subject: Science")))
                .thenReturn(serviceResponse);

        String requestBody = """
                {
                  "subject": "Science",
                  "ageGroup": "Junior Cycle",
                  "outcomes": "Understand acids and bases",
                  "topics": "pH scale, indicators, neutralisation",
                  "constraints": "40 minute class"
                }
                """;

        mockMvc.perform(post("/api/curriculum/map")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.mappedOutcomes[0].learning_outcome_ref").value("3.6"))
                .andExpect(jsonPath("$.result.mappedOutcomes[0].chunk_id").value(10))
                .andExpect(jsonPath("$.result.notes").value("The curriculum context was sufficient."))
                .andExpect(jsonPath("$.retrievedChunksIds[0]").value(1));
    }

    @Test
    void shouldReturnBadRequestWhenMappingServiceThrowsIllegalArgumentException() throws Exception {
        when(mappingService.mapLessonToCurriculum(contains("Subject: Science")))
                .thenThrow(new IllegalArgumentException("Unable to map curriculum request"));

        String requestBody = """
                {
                  "subject": "Science",
                  "ageGroup": "Junior Cycle",
                  "outcomes": "Understand acids and bases",
                  "topics": "pH scale",
                  "constraints": "40 minute class"
                }
                """;

        mockMvc.perform(post("/api/curriculum/map")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unable to map curriculum request"));
    }
}
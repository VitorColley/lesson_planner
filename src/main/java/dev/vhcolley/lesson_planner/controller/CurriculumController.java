package dev.vhcolley.lesson_planner.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.vhcolley.lesson_planner.model.CurriculumRequest;
import dev.vhcolley.lesson_planner.service.CurriculumMappingService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/curriculum")
public class CurriculumController {

    private final CurriculumMappingService mappingService;

    public CurriculumController(CurriculumMappingService mappingService){
            this.mappingService = mappingService;
    }

    @PostMapping("/map")
    public ResponseEntity<Map<String, Object>> mapCurriculum(@RequestBody CurriculumRequest request) {

        String lessonQuery = """
                Subject: %s
                Age Group: %s
                Topics: %s
                Learning Outcomes: %s
                Constraints: %s
                """.formatted(
                        request.subject(),
                        request.ageGroup(),
                        request.topics(),
                        request.outcomes(),
                        request.constraints()
                );
        Map<String, Object> response = mappingService.mapLessonToCurriculum(lessonQuery);
        return ResponseEntity.ok(response);
    }
}
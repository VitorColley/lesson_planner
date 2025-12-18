package dev.vhcolley.lesson_planner.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.vhcolley.lesson_planner.agent.CurriculumMappingAgent;
import dev.vhcolley.lesson_planner.model.CurriculumRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/curriculum")
public class CurriculumController {

        private final CurriculumMappingAgent agent;

        public CurriculumController(CurriculumMappingAgent agent){
            this.agent = agent;
        }

        @PostMapping("/map")
        public ResponseEntity<String> mapCurriculum(@RequestBody CurriculumRequest request) {
            String result = agent.mapCurriculum(request);
            return ResponseEntity.ok(result);
        }
        
}

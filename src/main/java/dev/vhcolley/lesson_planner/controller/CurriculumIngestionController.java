package dev.vhcolley.lesson_planner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.vhcolley.lesson_planner.domain.CurriculumDocument;
import dev.vhcolley.lesson_planner.service.CurriculumIngestionService;

@RestController
@RequestMapping("/api/curriculum")
public class CurriculumIngestionController {

    private final CurriculumIngestionService ingestionService;
    
    public CurriculumIngestionController(CurriculumIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<?> ingestCurriculum(@RequestParam("file") MultipartFile file) throws Exception{
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("File is empty");
        }

        CurriculumDocument document = ingestionService.ingestCurriculumPdf(
            file.getInputStream(), 
            file.getOriginalFilename()
        );

        return ResponseEntity.ok().body(
            java.util.Map.of(
                "documentId", document.getId(),
                "subject", document.getSubject(),
                "cycle", document.getCycle(),
                "title", document.getTitle()
            )
        );
    }
    
}

package dev.vhcolley.lesson_planner.integration;

import dev.vhcolley.lesson_planner.domain.CurriculumChunk;
import dev.vhcolley.lesson_planner.domain.CurriculumDocument;
import dev.vhcolley.lesson_planner.repository.CurriculumChunkRepository;
import dev.vhcolley.lesson_planner.repository.CurriculumDocumentRepository;
import dev.vhcolley.lesson_planner.service.CurriculumIngestionService;
import dev.vhcolley.lesson_planner.service.PdfTextExtractor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class CurriculumIngestionIntegrationTest {

    @Autowired
    private CurriculumIngestionService ingestionService;

    @Autowired
    private CurriculumDocumentRepository documentRepository;

    @Autowired
    private CurriculumChunkRepository chunkRepository;

    @MockitoBean
    private PdfTextExtractor pdfTextExtractor;

    @Test
    void shouldIngestCurriculumDocumentAndPersistChunks() throws Exception {
        String extractedText = """
                Junior Cycle
                Science
                Curriculum Specification

                Students should be able to investigate the properties of acids and bases.
                Students should understand the pH scale and neutralisation.
                This document contains learning outcomes for Junior Cycle Science.
                """;

        when(pdfTextExtractor.extractAllText(any()))
                .thenReturn(extractedText);

        CurriculumDocument document = ingestionService.ingestCurriculumPdf(
                new ByteArrayInputStream(new byte[]{1, 2, 3}),
                "junior-cycle-science.pdf"
        );

        assertNotNull(document);
        assertNotNull(document.getId());

        assertEquals("Science", document.getSubject());
        assertEquals("Junior Cycle", document.getCycle());
        assertEquals("Junior Cycle Science Curriculum Specification", document.getTitle());
        assertEquals("PDF", document.getSourceType());
        assertEquals("junior-cycle-science.pdf", document.getSourceRef());

        CurriculumDocument savedDocument = documentRepository.findById(document.getId())
                .orElseThrow();

        assertEquals("Science", savedDocument.getSubject());

        List<CurriculumChunk> chunks = chunkRepository.findAll()
                .stream()
                .filter(chunk -> chunk.getDocument().getId().equals(document.getId()))
                .toList();

        assertFalse(chunks.isEmpty());

        CurriculumChunk firstChunk = chunks.get(0);

        assertEquals(0, firstChunk.getChunkIndex());
        assertNotNull(firstChunk.getContent());
        assertFalse(firstChunk.getContent().isBlank());
        assertEquals("Science", firstChunk.getMetadata().get("detectedSubject"));
        assertEquals("Junior Cycle", firstChunk.getMetadata().get("detectedCycle"));
    }
}
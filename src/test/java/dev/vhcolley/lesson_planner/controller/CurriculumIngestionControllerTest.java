package dev.vhcolley.lesson_planner.controller;

import dev.vhcolley.lesson_planner.domain.CurriculumDocument;
import dev.vhcolley.lesson_planner.service.CurriculumIngestionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurriculumIngestionController.class)
@Import(GlobalExceptionHandler.class)
class CurriculumIngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurriculumIngestionService ingestionService;

    @Test
    void shouldUploadPdfAndReturnMetadata() throws Exception {
        CurriculumDocument document = new CurriculumDocument();
        ReflectionTestUtils.setField(document, "id", 1L);
        document.setSubject("Science");
        document.setCycle("Junior Cycle");
        document.setTitle("Junior Cycle Science Curriculum Specification");
        document.setSourceType("PDF");
        document.setSourceRef("science.pdf");

        when(ingestionService.ingestCurriculumPdf(any(), eq("science.pdf")))
                .thenReturn(document);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "science.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "dummy content".getBytes()
        );

        mockMvc.perform(multipart("/api/curriculum/ingest").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value(1))
                .andExpect(jsonPath("$.subject").value("Science"))
                .andExpect(jsonPath("$.cycle").value("Junior Cycle"))
                .andExpect(jsonPath("$.title").value("Junior Cycle Science Curriculum Specification"));
    }

    @Test
    void shouldReturnBadRequestWhenFileIsEmpty() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/curriculum/ingest").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenServiceThrowsIllegalArgumentException() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "unknown.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "unknown content".getBytes()
        );

        when(ingestionService.ingestCurriculumPdf(any(), eq("unknown.pdf")))
                .thenThrow(new IllegalArgumentException("Unable to determine subject"));

        mockMvc.perform(multipart("/api/curriculum/ingest").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unable to determine subject"));
    }
}
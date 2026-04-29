package dev.vhcolley.lesson_planner.service;

import dev.vhcolley.lesson_planner.domain.CurriculumChunk;
import dev.vhcolley.lesson_planner.domain.CurriculumDocument;
import dev.vhcolley.lesson_planner.repository.CurriculumChunkRepository;
import dev.vhcolley.lesson_planner.repository.CurriculumDocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurriculumIngestionServiceTest {

    @Mock
    private PdfTextExtractor pdfTextExtractor;

    @Mock
    private Chunker chunker;

    @Mock
    private CurriculumDocumentRepository documentRepository;

    @Mock
    private CurriculumChunkRepository chunkRepository;

    @Mock
    private CurriculumMetadataClassifier metadataClassifier;

    @InjectMocks
    private CurriculumIngestionService ingestionService;

    @SuppressWarnings("null")
    @Test
    void shouldExtractMetadataSaveDocumentAndChunks() throws Exception {
        String fullText = """
                Junior Cycle
                Science
                Curriculum Specification
                """;

        CurriculumMetadata metadata = new CurriculumMetadata(
                "Junior Cycle Science Curriculum Specification",
                "Science",
                "Junior Cycle"
        );

        CurriculumDocument savedDocument = new CurriculumDocument();
        
        savedDocument.setSubject("Science");
        savedDocument.setCycle("Junior Cycle");
        savedDocument.setTitle("Junior Cycle Science Curriculum Specification");
        savedDocument.setSourceType("PDF");
        savedDocument.setSourceRef("science.pdf");

        when(pdfTextExtractor.extractAllText(any())).thenReturn(fullText);
        when(metadataClassifier.extractMetadata(fullText, "science.pdf")).thenReturn(metadata);
        when(documentRepository.save(any(CurriculumDocument.class))).thenReturn(savedDocument);
        when(chunker.chunk(fullText, 3500, 400)).thenReturn(List.of("chunk one", "chunk two"));

        CurriculumDocument result = ingestionService.ingestCurriculumPdf(
                new ByteArrayInputStream(new byte[]{1, 2, 3}),
                "science.pdf"
        );

        assertEquals("Science", result.getSubject());
        assertEquals("Junior Cycle", result.getCycle());
        assertEquals("Junior Cycle Science Curriculum Specification", result.getTitle());

        ArgumentCaptor<CurriculumDocument> documentCaptor =
                ArgumentCaptor.forClass(CurriculumDocument.class);

        verify(documentRepository).save(documentCaptor.capture());

        CurriculumDocument documentToSave = documentCaptor.getValue();

        assertEquals("Science", documentToSave.getSubject());
        assertEquals("Junior Cycle", documentToSave.getCycle());
        assertEquals("Junior Cycle Science Curriculum Specification", documentToSave.getTitle());
        assertEquals("PDF", documentToSave.getSourceType());
        assertEquals("science.pdf", documentToSave.getSourceRef());

        ArgumentCaptor<CurriculumChunk> chunkCaptor =
                ArgumentCaptor.forClass(CurriculumChunk.class);

        verify(chunkRepository, times(2)).save(chunkCaptor.capture());

        List<CurriculumChunk> savedChunks = chunkCaptor.getAllValues();

        assertEquals(0, savedChunks.get(0).getChunkIndex());
        assertEquals("chunk one", savedChunks.get(0).getContent());

        assertEquals(1, savedChunks.get(1).getChunkIndex());
        assertEquals("chunk two", savedChunks.get(1).getContent());

        assertEquals("Science", savedChunks.get(0).getMetadata().get("detectedSubject"));
        assertEquals("Junior Cycle", savedChunks.get(0).getMetadata().get("detectedCycle"));
    }
}
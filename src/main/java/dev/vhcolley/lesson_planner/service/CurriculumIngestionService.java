package dev.vhcolley.lesson_planner.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.vhcolley.lesson_planner.domain.CurriculumChunk;
import dev.vhcolley.lesson_planner.domain.CurriculumDocument;
import dev.vhcolley.lesson_planner.repository.CurriculumChunkRepository;
import dev.vhcolley.lesson_planner.repository.CurriculumDocumentRepository;

@Service
public class CurriculumIngestionService {

    private final PdfTextExtractor pdfTextExtractor;
    private final Chunker chunker;
    private final CurriculumDocumentRepository documentRepository;
    private final CurriculumChunkRepository chunkRepository;

    public CurriculumIngestionService(
        PdfTextExtractor pdfTextExtractor,
        Chunker chunker,
        CurriculumDocumentRepository documentRepository,
        CurriculumChunkRepository chunkRepository
    ) {
        this.pdfTextExtractor = pdfTextExtractor;
        this.chunker = chunker;
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
    }
    
    @Transactional
    public CurriculumDocument ingestScienceSpecfificPdf(InputStream pdfStream, String sorceRef) throws Exception {
        String fullText = pdfTextExtractor.extractAllText(pdfStream);

        CurriculumDocument document = new CurriculumDocument();
        document.setSubject("Science");
        document.setCycle("Junior Cycle");
        document.setTitle("Junior Cycle Science Curriculum Specification");
        document.setSourceType("PDF");
        document.setSourceRef(sorceRef);
        document = documentRepository.save(document);

        List<String> chunks = chunker.chunk(fullText, 3500, 400);

        int idx = 0;
        for (String c : chunks) {
            CurriculumChunk chunk = new CurriculumChunk();
            chunk.setDocument(document);
            chunk.setChunkIndex(idx++);
            chunk.setContent(c);

            Map<String, Object> meta = new HashMap<>();
            meta.put("pipeline", "v1_char_chunker");
            meta.put("language", "en");
            chunk.setMetadata(meta);

            chunkRepository.save(chunk);
        }
        return document;
    }
}

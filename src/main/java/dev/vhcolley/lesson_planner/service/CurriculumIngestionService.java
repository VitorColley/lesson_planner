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
    private final CurriculumMetadataClassifier metadataClassifier;

    public CurriculumIngestionService(
        PdfTextExtractor pdfTextExtractor,
        Chunker chunker,
        CurriculumDocumentRepository documentRepository,
        CurriculumChunkRepository chunkRepository,
        CurriculumMetadataClassifier metadataClassifier
    ) {
        this.pdfTextExtractor = pdfTextExtractor;
        this.chunker = chunker;
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.metadataClassifier = metadataClassifier;
    }
    
    @Transactional
    public CurriculumDocument ingestCurriculumPdf(InputStream pdfStream, String sourceRef) throws Exception {
        String fullText = pdfTextExtractor.extractAllText(pdfStream);

        CurriculumMetadata metadata = metadataClassifier.extractMetadata(fullText, sourceRef);


        CurriculumDocument document = new CurriculumDocument();
        document.setSubject(metadata.subject());
        document.setCycle(metadata.cycle());
        document.setTitle(metadata.title());
        document.setSourceType("PDF");
        document.setSourceRef(sourceRef);
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
            meta.put("detectedSubject", metadata.subject());
            meta.put("detectedCycle", metadata.cycle());
            chunk.setMetadata(meta);

            chunkRepository.save(chunk);
        }
        return document;
    }
}

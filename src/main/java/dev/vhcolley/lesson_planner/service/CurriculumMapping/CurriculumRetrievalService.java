// References:
// - PostgreSQL full-text search for efficient curriculum chunk retrieval
// https://www.postgresql.org/docs/current/textsearch.html

package dev.vhcolley.lesson_planner.service.CurriculumMapping;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

import dev.vhcolley.lesson_planner.domain.CurriculumChunk;
import dev.vhcolley.lesson_planner.domain.CurriculumDocument;
import dev.vhcolley.lesson_planner.repository.CurriculumChunkRepository;
import dev.vhcolley.lesson_planner.repository.CurriculumDocumentRepository;

@Service
public class CurriculumRetrievalService {
    
    private final CurriculumChunkRepository curriculumChunk;
    private final CurriculumDocumentRepository curriculumDocument;

    public CurriculumRetrievalService(CurriculumChunkRepository curriculumChunk, CurriculumDocumentRepository curriculumDocument) {
        this.curriculumChunk = curriculumChunk;
        this.curriculumDocument = curriculumDocument;
    }

    public List<CurriculumChunk> retrieveTopChunks(String query, int limit){
        int safeLimit = Math.max(1, Math.min(limit,25));
        String q = normaliseQuery(query);

        List<CurriculumChunk> hits = q.isBlank() ? List.of() : curriculumChunk.searchByContent(q, safeLimit);
        if(!hits.isEmpty()){
            return hits;
        }

        Long latestDocId = curriculumDocument.findTopByOrderByIdDesc().map(CurriculumDocument::getId).orElse(null);

        if(latestDocId == null){
            return List.of();
        }

        return curriculumChunk.findByDocumentIdOrderByChunkIndexAsc(latestDocId, PageRequest.of(0, safeLimit));
    }

    private String normaliseQuery(String query){
        if(query == null){
            return "";
        }
        return query.replaceAll("\\s+", " ").trim();
    }
}

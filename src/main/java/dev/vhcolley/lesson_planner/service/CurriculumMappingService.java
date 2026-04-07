package dev.vhcolley.lesson_planner.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.vhcolley.lesson_planner.ai.CurriculumMappingAgent;
import dev.vhcolley.lesson_planner.domain.CurriculumChunk;
import dev.vhcolley.lesson_planner.domain.MappingRun;
import dev.vhcolley.lesson_planner.repository.MappingRunRepository;

@Service
public class CurriculumMappingService {
    
    private final CurriculumRetrievalService curriculumRetrieval;
    private final CurriculumMappingAgent curriculumMappingAgent;
    private final MappingRunRepository mappingRunRepository;

    public CurriculumMappingService(CurriculumRetrievalService curriculumRetrieval, CurriculumMappingAgent curriculumMappingAgent, MappingRunRepository mappingRunRepository) {
        this.curriculumRetrieval = curriculumRetrieval;
        this.curriculumMappingAgent = curriculumMappingAgent;
        this.mappingRunRepository = mappingRunRepository;
    }

    @Transactional
    public Map<String, Object> mapLessonToCurriculum(String lessonInput){
        List<CurriculumChunk> chunks = curriculumRetrieval.retrieveTopChunks(lessonInput, 10);
        
        String aiJson = curriculumMappingAgent.mapWithRetrievedChunks(lessonInput, chunks);

        MappingRun mappingRun = new MappingRun();
        mappingRun.setLessonInput(lessonInput);
        mappingRun.setRetrievedChunkIds(chunks.stream().map(CurriculumChunk::getId).toList());

        Map<String, Object> parsed = JsonUtil.safeParseJsonObject(aiJson);
        mappingRun.setAiResponseJson(parsed);

        mappingRunRepository.save(mappingRun);

        return Map.of(
            "retrievedChunksIds", mappingRun.getRetrievedChunkIds(),
            "result", parsed
        );
    }
}

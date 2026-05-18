// References:
// - Spring AI ChatClient documentation for building prompts and calling the API
// https://docs.spring.io/spring-ai/reference/api/chatclient.html

package dev.vhcolley.lesson_planner.ai;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import dev.vhcolley.lesson_planner.domain.CurriculumChunk;
import dev.vhcolley.lesson_planner.dto.CurriculumRequest;

@Component
public class CurriculumMappingAgent {

    private final ChatClient chatClient;

    public CurriculumMappingAgent(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    @SuppressWarnings("null")
    public String mapCurriculum(CurriculumRequest request){
        
        String systemPrompt = """
        You are an AI curriculum mapping agent.

        Your responsibilities:
        - Adapt the curriculum to the learner's age group and subject
        - Explain WHY outcomes are mapped to units
        - Highlight pedagogical reasoning

        Output STRICT JSON only in the following structure:

        {
        "context": {
            "subject": "",
            "ageGroup": ""
        },
        "units": [
            {
            "title": "",
            "topics": [],
            "learningOutcomes": [],
            "rationale": ""
            }
        ],
        "pedagogicalNotes": [],
        "gaps": [],
        "overlaps": []
        }
        """;

        String userPrompt = """
        Subject:
        %s

        Age Group:
        %s

        Learning Outcomes:
        %s

        Topics:
        %s

        Constraints:
        %s
        """.formatted(
                request.subject(),
                request.ageGroup(),
                request.outcomes(),
                request.topics(),
                request.constraints()
        );

        return chatClient.prompt().system(systemPrompt).user(userPrompt).call().content();
    }

    @SuppressWarnings("null")
    public String mapWithRetrievedChunks(String lessonInput, List<CurriculumChunk> chunks){
        String context = chunks.stream().map(c -> "- Chunk #" + c.getId() + "\n" + c.getContent()).collect(Collectors.joining("\n\n"));

        String prompt = """
        You are an AI curriculum mapping agent with access to relevant curriculum information.

        Task:
        Map the teacher's lesson content to the most relevant curriculum learning outcomes.

        Rules:
        - Use ONLY the provided curriculum context.
        - If context is insufficient, return an empty outcomes list and explain why.
        - Output MUST be valid Json.

        Required JSON structure:
        {
          "mappedOutcomes": [
            {
              "chunk_id": 123,
              "learning_outcome_ref": "UNKNOWN_OR_EXTRACTED",
              "justification": "..."
            }
          ],
          "notes": "..."
        }

        CURRICULUM CONTEXT (retrieved chunks):
        %s

        TEACHER LESSON INPUT:
        %s
        """.formatted(context, lessonInput);

        return chatClient.prompt(prompt).call().content();
    }
}

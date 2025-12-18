package dev.vhcolley.lesson_planner.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import dev.vhcolley.lesson_planner.model.CurriculumRequest;

@Service
public class CurriculumMappingAgent {

    private final ChatClient chatClient;

    public CurriculumMappingAgent(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    public String mapCurriculum(CurriculumRequest request){
        
        String systemPrompt = """
        You are an AI curriculum mapping agent.

        Your responsibilities:
        - Adapt the curriculum to the learner's age group and subject
        - Explain WHY outcomes are mapped to units
        - Highlight pedagogical reasoning
        - Ask clarifying questions ONLY if essential information is missing

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
        "overlaps": [],
        "clarifyingQuestions": []
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

        return chatClient.prompt()
        .system(systemPrompt)
        .user(userPrompt)
        .call()
        .content();
    }

}

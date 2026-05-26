// References:
// - Spring AI ChatClient documentation for building prompts and calling the API
// https://docs.spring.io/spring-ai/reference/api/chatclient.html

package dev.vhcolley.lesson_planner.ai;

import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import dev.vhcolley.lesson_planner.dto.ActivityCard;
import dev.vhcolley.lesson_planner.dto.LessonState;
import dev.vhcolley.lesson_planner.dto.MappedOutcome;

@Component
public class ActivityEnhancementAgent {

    private final ChatClient chatClient;

    public ActivityEnhancementAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @SuppressWarnings("null")
    public String enhanceActivity(ActivityCard card, LessonState state) {
        String mappedOutcomes = state.mappedOutcomes() == null
                ? ""
                : state.mappedOutcomes()
                        .stream()
                        .map(this::formatMappedOutcome)
                        .collect(Collectors.joining("\n"));

        String materials = card.materials() == null
                ? ""
                : card.materials().stream().collect(Collectors.joining(", "));

        String systemPrompt = """
                You are an expert lesson planning assistant.

                Your task is to adapt an existing classroom activity template
                to a specific lesson context.

                Important rules:
                - Do NOT invent a completely new activity.
                - Keep the same activity title, method, stage, duration, and materials.
                - Only rewrite the text fields:
                  description, whyThisFits, teacherRole, studentTask.
                - Make the activity specific to the topic, learning outcomes, age group, and constraints.
                - Keep the language clear, practical, and teacher-friendly.
                - Output STRICT valid JSON only.
                - Do not include markdown, comments, or explanations outside the JSON.

                Required JSON structure:
                {
                  "description": "",
                  "whyThisFits": "",
                  "teacherRole": "",
                  "studentTask": ""
                }
                """;

        String userPrompt = """
                LESSON CONTEXT:
                Subject: %s
                Age Group: %s
                Topic: %s
                Current Stage: %s
                Total Duration: %d minutes
                Remaining Time: %d minutes
                Mapped Learning Outcomes: %s
                Constraints: %s

                ACTIVITY TEMPLATE:
                ID: %s
                Title: %s
                Method: %s
                Stage: %s
                Duration: %d minutes
                Materials: %s

                Original Description:
                %s

                Original Why This Fits:
                %s

                Original Teacher Role:
                %s

                Original Student Task:
                %s
                """.formatted(
                state.subject(),
                state.ageGroup(),
                state.topic(),
                state.currentStage(),
                state.totalDurationMinutes(),
                state.remainingMinutes(),
                mappedOutcomes,
                state.constraints(),
                card.id(),
                card.title(),
                card.method(),
                card.lessonStage(),
                card.durationMinutes(),
                materials,
                card.description(),
                card.whyThisFits(),
                card.teacherRole(),
                card.studentTask()
        );

        return chatClient
                .prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }
    private String formatMappedOutcome(MappedOutcome outcome) {
        return """
                - Outcome Ref: %s
                Curriculum Text: %s
                Justification: %s
                Chunk ID: %s
                """.formatted(
                outcome.learningOutcomeRef(),
                outcome.curriculumText(),
                outcome.justification(),
                outcome.chunkId()
        );
    }
}
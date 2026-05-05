package dev.vhcolley.lesson_planner.service;

import dev.vhcolley.lesson_planner.ai.ActivityEnhancementAgent;
import dev.vhcolley.lesson_planner.dto.ActivityCard;
import dev.vhcolley.lesson_planner.dto.EnhancedActivityText;
import dev.vhcolley.lesson_planner.dto.LessonState;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiActivityEnhancerService {
        
        private final ActivityEnhancementAgent activityEnhancementAgent;
        private final ObjectMapper objectMapper;

        public AiActivityEnhancerService(
                ActivityEnhancementAgent activityEnhancementAgent,
                ObjectMapper objectMapper
        ) {
                this.activityEnhancementAgent = activityEnhancementAgent;
                this.objectMapper = objectMapper;
        }

        public ActivityCard enhance(ActivityCard card, LessonState state) {        
        try{
                String json = activityEnhancementAgent.enhanceActivity(card, state);
                
                EnhancedActivityText enhancedText = objectMapper.readValue(json, EnhancedActivityText.class);

                return new ActivityCard(
                card.id(),
                card.title(),
                card.method(),
                card.lessonStage(),
                card.durationMinutes(),
                enhancedText.description(),
                enhancedText.whyThisFits(),
                enhancedText.teacherRole(),
                enhancedText.studentTask(),
                card.materials()
        );
        }catch(Exception ex){
                return card;                
        }
    }
}
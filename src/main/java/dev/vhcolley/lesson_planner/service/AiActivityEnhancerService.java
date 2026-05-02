package dev.vhcolley.lesson_planner.service;

import dev.vhcolley.lesson_planner.dto.ActivityCard;
import dev.vhcolley.lesson_planner.dto.EnhancedActivityText;
import dev.vhcolley.lesson_planner.dto.LessonState;
import org.springframework.stereotype.Service;

@Service
public class AiActivityEnhancerService {

    public ActivityCard enhance(ActivityCard card, LessonState state) {
        EnhancedActivityText enhancedText = enhanceText(card, state);

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
    }

    private EnhancedActivityText enhanceText(ActivityCard card, LessonState state) {
        String description = card.description()
                + " The activity should focus specifically on the topic: "
                + state.topic()
                + ".";

        String whyThisFits = card.whyThisFits()
                + " It is aligned with the selected learning outcome(s): "
                + String.join(", ", state.mappedOutcomes())
                + ".";

        String teacherRole = card.teacherRole()
                + " The teacher should connect the activity clearly to "
                + state.topic()
                + ".";

        String studentTask = card.studentTask()
                + " Students should apply their understanding of "
                + state.topic()
                + ".";

        return new EnhancedActivityText(
                description,
                whyThisFits,
                teacherRole,
                studentTask
        );
    }
}
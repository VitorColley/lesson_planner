package dev.vhcolley.lesson_planner.service;

import dev.vhcolley.lesson_planner.dto.ActivityCard;
import dev.vhcolley.lesson_planner.dto.ActivityTemplate;
import dev.vhcolley.lesson_planner.dto.LessonState;
import dev.vhcolley.lesson_planner.dto.SelectedActivity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivitySuggestionService {

    private final ActivityBankService activityBankService;
    private final AiActivityEnhancerService aiActivityEnhancerService;

    public ActivitySuggestionService(
            ActivityBankService activityBankService,
            AiActivityEnhancerService aiActivityEnhancerService
    ) {
        this.activityBankService = activityBankService;
        this.aiActivityEnhancerService = aiActivityEnhancerService;
    }

    public List<ActivityCard> suggestActivities(LessonState state) {
        List<String> selectedActivityIds = state.selectedActivities()
                .stream()
                .map(SelectedActivity::id)
                .toList();

        List<ActivityTemplate> filtered = activityBankService.loadActivities().stream()
                .filter(activity -> matchesStage(activity, state.currentStage()))
                .filter(activity -> fitsTime(activity, state.remainingMinutes()))
                .filter(activity -> matchesSubject(activity, state.subject()))
                .filter(activity -> matchesAgeGroup(activity, state.ageGroup()))
                .filter(activity -> notAlreadySelected(activity, selectedActivityIds))
                .limit(3)
                .toList();

        return filtered.stream()
                .map(activity -> toActivityCard(activity, state))
                .map(card -> aiActivityEnhancerService.enhance(card, state))
                .toList();
    }

    private ActivityCard toActivityCard(ActivityTemplate activity, LessonState state) {
        int estimatedDuration = Math.min(
                activity.durationMax(),
                state.remainingMinutes()
        );

        return new ActivityCard(
                activity.id(),
                activity.title(),
                activity.method(),
                activity.lessonStage(),
                estimatedDuration,
                adaptDescription(activity, state),
                buildWhyThisFits(activity, state),
                activity.teacherRole(),
                activity.studentTask(),
                activity.materials()
        );
    }

    private String adaptDescription(ActivityTemplate activity, LessonState state) {
        return activity.description()
                + " This activity is adapted to the topic: "
                + state.topic()
                + ".";
    }

    private String buildWhyThisFits(ActivityTemplate activity, LessonState state) {
        String outcomePart = state.mappedOutcomes() != null && !state.mappedOutcomes().isEmpty()
                ? " It supports learning outcome(s): " + String.join(", ", state.mappedOutcomes()) + "."
                : "";

        return "This activity uses " + activity.method().toLowerCase()
                + " and is suitable for the "
                + activity.lessonStage().toLowerCase()
                + " stage of the lesson."
                + outcomePart;
    }

    private boolean matchesStage(ActivityTemplate activity, String lessonStage) {
        return activity.lessonStage().equalsIgnoreCase(lessonStage);
    }

    private boolean fitsTime(ActivityTemplate activity, int remainingMinutes) {
        return activity.durationMin() <= remainingMinutes;
    }

    private boolean matchesSubject(ActivityTemplate activity, String subject) {
        return activity.subjects().stream()
                .anyMatch(s -> s.equalsIgnoreCase(subject) || s.equalsIgnoreCase("General"));
    }

    private boolean matchesAgeGroup(ActivityTemplate activity, String ageGroup) {
        return activity.ageGroups().stream()
                .anyMatch(a -> a.equalsIgnoreCase(ageGroup));
    }

    private boolean notAlreadySelected(ActivityTemplate activity, List<String> selectedActivityIds) {
        return selectedActivityIds == null || !selectedActivityIds.contains(activity.id());
    }
}
package dev.vhcolley.lesson_planner.service.LessonBuilder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import dev.vhcolley.lesson_planner.dto.ActivityTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivityBankServiceTest {

    @Autowired
    private ActivityBankService activityBankService;

    @Test
    void shouldLoadActivityBankFromJsonFile() {
        List<ActivityTemplate> activities = activityBankService.loadActivities();

        assertNotNull(activities);
        assertFalse(activities.isEmpty());

        ActivityTemplate firstActivity = activities.get(0);

        assertNotNull(firstActivity.id());
        assertNotNull(firstActivity.title());
        assertNotNull(firstActivity.lessonStage());
        assertTrue(firstActivity.durationMin() > 0);
        assertTrue(firstActivity.durationMax() >= firstActivity.durationMin());
    }
}
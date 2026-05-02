package dev.vhcolley.lesson_planner.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.vhcolley.lesson_planner.dto.ActivityTemplate;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class ActivityBankService {

    private final ObjectMapper objectMapper;

    public ActivityBankService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<ActivityTemplate> loadActivities() {
        try {
            ClassPathResource resource = new ClassPathResource("activity-bank.json");

            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<ActivityTemplate>>() {}
                );
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load activity bank.", ex);
        }
    }
}
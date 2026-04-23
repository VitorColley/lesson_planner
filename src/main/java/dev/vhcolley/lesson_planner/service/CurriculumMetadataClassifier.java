package dev.vhcolley.lesson_planner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CurriculumMetadataClassifier {

    public CurriculumMetadata extractMetadata(String fullText, String sourceRef) {
        List<String> candidates = extractCandidateLines(fullText, 10);
        String headerPreview = String.join(" ", candidates);

        String subject = detectSubject(headerPreview, fullText);
        String cycle = detectCycle(headerPreview, fullText);
        String title = buildTitle(subject, cycle, candidates, sourceRef);

        return new CurriculumMetadata(title, subject, cycle);
    }

    private String detectSubject(String headerPreview, String fullText) {
        String normalized = (headerPreview + " " + firstLines(fullText, 15)).toLowerCase();

        if (normalized.contains("science")) {
            return "Science";
        }
        if (normalized.contains("mathematics") || normalized.contains("math")) {
            return "Mathematics";
        }
        if (normalized.contains("english")) {
            return "English";
        }
        if (normalized.contains("biology")) {
            return "Biology";
        }
        if (normalized.contains("chemistry")) {
            return "Chemistry";
        }
        if (normalized.contains("physics")) {
            return "Physics";
        }
        if (normalized.contains("history")) {
            return "History";
        }
        if (normalized.contains("geography")) {
            return "Geography";
        }

        throw new IllegalArgumentException("Unable to determine subject from document header/content.");
    }

    private String detectCycle(String headerPreview, String fullText) {
        String normalized = (headerPreview + " " + firstLines(fullText, 20)).toLowerCase();

        if (normalized.contains("junior cycle")) {
            return "Junior Cycle";
        }
        if (normalized.contains("leaving certificate")) {
            return "Leaving Certificate";
        }
        if (normalized.contains("primary curriculum") || normalized.contains("primary")) {
            return "Primary";
        }

        return "Unknown";
    }

    private String buildTitle(String subject, String cycle, List<String> candidates, String sourceRef) {
        if (!"Unknown".equals(subject) && !"Unknown".equals(cycle)) {
            return cycle + " " + subject + " Curriculum Specification";
        }

        if (!"Unknown".equals(subject)) {
            return subject + " Curriculum Specification";
        }

        if (!candidates.isEmpty()) {
            return String.join(" ", candidates.subList(0, Math.min(3, candidates.size())));
        }

        return sourceRef != null ? sourceRef : "Untitled Curriculum Document";
    }

    private List<String> extractCandidateLines(String fullText, int maxLines) {
        String[] lines = fullText.split("\\R");
        List<String> candidates = new ArrayList<>();

        for (String line : lines) {
            String cleaned = line.trim();

            if (!cleaned.isBlank() && cleaned.length() > 2) {
                candidates.add(cleaned);
            }

            if (candidates.size() >= maxLines) {
                break;
            }
        }

        return candidates;
    }

    private String firstLines(String fullText, int maxLines) {
        String[] lines = fullText.split("\\R");
        StringBuilder builder = new StringBuilder();
        int count = 0;

        for (String line : lines) {
            String cleaned = line.trim();

            if (!cleaned.isBlank()) {
                builder.append(cleaned).append(" ");
                count++;
            }

            if (count >= maxLines) {
                break;
            }
        }

        return builder.toString().trim();
    }
}
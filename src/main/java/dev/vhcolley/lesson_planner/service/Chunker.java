package dev.vhcolley.lesson_planner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Chunker {
    public List<String> chunk(String text, int chunkSizeChars, int overlapChars) {
        String cleaned = normalise(text);
        List<String> chunks = new ArrayList<>();
        if (cleaned.isBlank()) return chunks;

        int start = 0;
        while (start < cleaned.length()) {
            int end = Math.min(start + chunkSizeChars, cleaned.length());
            String chunk = cleaned.substring(start, end).trim();
            if (!chunk.isBlank()) chunks.add(chunk);

            if(end == cleaned.length()) break;
            start = Math.max(0, end - overlapChars);
        }
        return chunks;        
    }

    private String normalise(String s) {
        return s
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .replaceAll("[ \\t]+", "")
            .replaceAll("\\n{3,}", "\n\n")
            .trim();
    }
}

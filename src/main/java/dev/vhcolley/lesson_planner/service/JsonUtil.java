package dev.vhcolley.lesson_planner.service;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil(){}

    public static Map<String, Object> safeParseJsonObject(String maybeJson){
        try{
            if(maybeJson == null){
                return Map.of("raw", "");
            }
            String trimmed = stripCodeFences(maybeJson.trim());
            if(trimmed.isBlank()){
                return Map.of("raw", "");
            }
            return MAPPER.readValue(trimmed, new TypeReference<>() {});
        }catch(Exception e){
            return Map.of(
                "raw", maybeJson == null ? "" : maybeJson,
                "parseError", e.getMessage()
            );
        }
    }
    private static String stripCodeFences(String s){
        if(s.startsWith("```")){
            int firstNewLine = s.indexOf('\n');
            if(firstNewLine > 0){
                s = s.substring(firstNewLine + 1);
                int lastFence = s.lastIndexOf("```");
                if(lastFence > 0){
                    s = s.substring(0, lastFence);
                }
            }
        }
        return s.trim();
    }
}

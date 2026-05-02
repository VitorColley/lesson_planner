package dev.vhcolley.lesson_planner.model;

public record CurriculumRequest(
    
    String subject, 
    String ageGroup,
    String outcomes, 
    String topics, 
    String constraints
    
) {

}

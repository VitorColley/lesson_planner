package dev.vhcolley.lesson_planner.service.CurriculumMapping;

import org.junit.jupiter.api.Test;

import dev.vhcolley.lesson_planner.dto.CurriculumMetadata;
import dev.vhcolley.lesson_planner.service.CurriculumMapping.CurriculumIngestion.CurriculumMetadataClassifier;

import static org.junit.jupiter.api.Assertions.*;

class CurriculumMetadataClassifierTest {

    private final CurriculumMetadataClassifier classifier =
            new CurriculumMetadataClassifier();

    @Test
    void shouldDetectJuniorCycleScienceMetadata() {
        String text = """
                Junior Cycle
                Science
                Curriculum Specification
                Learning outcomes and strands
                """;

        CurriculumMetadata metadata = classifier.extractMetadata(text, "science.pdf");

        assertEquals("Science", metadata.subject());
        assertEquals("Junior Cycle", metadata.cycle());
        assertEquals("Junior Cycle Science Curriculum Specification", metadata.title());
    }

    @Test
    void shouldDetectJuniorCycleMathematicsMetadata() {
        String text = """
                Junior Cycle
                Mathematics
                Curriculum Specification
                Number, algebra, geometry and statistics
                """;

        CurriculumMetadata metadata = classifier.extractMetadata(text, "maths.pdf");

        assertEquals("Mathematics", metadata.subject());
        assertEquals("Junior Cycle", metadata.cycle());
        assertEquals("Junior Cycle Mathematics Curriculum Specification", metadata.title());
    }

    @Test
    void shouldDetectSeniorCycleBiologyMetadata() {
        String text = """
                Senior Cycle
                Biology
                Specification
                Students develop knowledge of living systems
                """;

        CurriculumMetadata metadata = classifier.extractMetadata(text, "biology.pdf");

        assertEquals("Biology", metadata.subject());
        assertEquals("Senior Cycle", metadata.cycle());
        assertEquals("Senior Cycle Biology Curriculum Specification", metadata.title());
    }

    @Test
    void shouldReturnUnknownCycleWhenCycleCannotBeDetected() {
        String text = """
                Science
                Curriculum Specification
                Chemical world and biological world
                """;

        CurriculumMetadata metadata = classifier.extractMetadata(text, "science.pdf");

        assertEquals("Science", metadata.subject());
        assertEquals("Unknown", metadata.cycle());
        assertEquals("Science Curriculum Specification", metadata.title());
    }

    @Test
    void shouldThrowExceptionWhenSubjectCannotBeDetected() {
        String text = """
                General Education Document
                Curriculum Overview
                No recognised subject appears here
                """;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> classifier.extractMetadata(text, "unknown.pdf")
        );

        assertTrue(exception.getMessage().contains("Unable to determine subject"));
    }
}
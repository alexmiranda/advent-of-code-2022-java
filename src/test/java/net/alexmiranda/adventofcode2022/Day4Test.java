package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class Day4Test {
    @Test
    public void testCountAssignmentsFullyContained() throws URISyntaxException, IOException {
        assertEquals(477, Day4.countAssignmentsFullyContained());
    }

    @Test
    public void testCountAssignmentsOverlapping() throws URISyntaxException, IOException {
        assertEquals(830, Day4.countAssignmentsOverlapping());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
                '2-4,6-8', false
                '2-3,4-5', false
                '5-7,7-9', false
                '2-8,3-7', true
                '6-6,4-6', true
                '2-6,4-8', false
            """)
    public void testFullyContained(String input, boolean expectedResult) {
        assertEquals(expectedResult, Day4.fullyContained(input));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
                '2-4,6-8', false
                '2-3,4-5', false
                '5-7,7-9', true
                '2-8,3-7', true
                '6-6,4-6', true
                '2-6,4-8', true
            """)
    public void testOverlap(String input, boolean expectedResult) {
        assertEquals(expectedResult, Day4.overlap(input));
    }
}

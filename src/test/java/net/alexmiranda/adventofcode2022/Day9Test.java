package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class Day9Test {
    private static final String example = """
            R 4
            U 4
            L 3
            D 1
            R 4
            D 1
            L 5
            R 2
            """;

    @Test
    public void testExamplePart1() {
        try (var reader = new StringReader(example)) {
            int count = Day9.countVisited(reader, 2);
            assertEquals(13, count);
        }
    }
    
    @Test
    public void testPuzzleInputPart1() {
        int count = Day9.countVisited(2);
        assertEquals(6067, count);
    }
    
    @Test
    public void testExamplePart2() {
        try (var reader = new StringReader(example)) {
            int count = Day9.countVisited(reader, 10);
            assertEquals(1, count);
        }
    }
    
    @Test
    public void testPuzzleInputPart2() {
        int count = Day9.countVisited(10);
        assertEquals(2471, count);
    }
}

package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class Day18Test {
    private static final String EXAMPLE = """
            2,2,2
            1,2,2
            3,2,2
            2,1,2
            2,3,2
            2,2,1
            2,2,3
            2,2,4
            2,2,6
            1,2,5
            3,2,5
            2,1,5
            2,3,5
            """;

    @Test
    public void testExamplePart1() throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            var answer = Day18.part1(reader);
            assertEquals(64, answer);
        }
    }

    @Test
    public void testPuzzleInputPart1() throws IOException {
        try (var reader = Day18.puzzleInput()) {
            var answer = Day18.part1(reader);
            assertEquals(4474, answer);
        }
    }

    @Test
    public void testParse() throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            var coords = Day18.parseInput(reader);
            assertEquals(13, coords.size());
        }
    }
}

package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class Day19Test {
    private static final String EXAMPLE = """
            Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
            Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
            """;

    @Test
    public void testExamplePart1() throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            var factory = new Day19.Factory();
            factory.readBlueprints(reader);
            var result = factory.determineQualityLevel(24);
            assertEquals(33, result);
        }
    }

    @Test
    public void testPuzzleInputPart1() throws IOException {
        try (var reader = Day19.puzzleInput()) {
            var factory = new Day19.Factory();
            factory.readBlueprints(reader);
            var result = factory.determineQualityLevel(24);
            assertEquals(1356, result);
        }
    }

    @Test
    public void testDetailedExamplePart1() throws IOException {
        var detailedExample = EXAMPLE.substring(0, EXAMPLE.indexOf("\n"));
        try (var reader = new StringReader(detailedExample)) {
            var factory = new Day19.Factory();
            factory.readBlueprints(reader);
            var result = factory.determineQualityLevel(24);
            assertEquals(9, result);
        }
    }
}

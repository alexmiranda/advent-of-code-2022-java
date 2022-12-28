package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
    public void testPuzzleInputPart2() throws IOException {
        try (var reader = Day19.puzzleInput()) {
            var factory = new Day19.Factory();
            factory.readBlueprints(reader, 3);
            var result = factory.multiplyMaximumOpenGeodes(32);
            assertEquals(27720, result);
        }
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1,24,9
            2,24,12
            1,32,56
            2,32,62
            """)
    public void testDetailedExample(int id, int minutes, int expectedResult) throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            var factory = new Day19.Factory();
            factory.readBlueprints(reader);
            var blueprint = factory.blueprints.stream().filter(p -> p.id() == id).findFirst().get();
            var openGeodes = factory.maximumOpenGeodes(blueprint, minutes);
            assertEquals(expectedResult, openGeodes);
        }
    }
}

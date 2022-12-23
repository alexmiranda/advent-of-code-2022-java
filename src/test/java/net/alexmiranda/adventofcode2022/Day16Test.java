package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Execution(ExecutionMode.SAME_THREAD)
public class Day16Test {
    private static final String EXAMPLE = """
            Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
            Valve BB has flow rate=13; tunnels lead to valves CC, AA
            Valve CC has flow rate=2; tunnels lead to valves DD, BB
            Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
            Valve EE has flow rate=3; tunnels lead to valves FF, DD
            Valve FF has flow rate=0; tunnels lead to valves EE, GG
            Valve GG has flow rate=0; tunnels lead to valves FF, HH
            Valve HH has flow rate=22; tunnel leads to valve GG
            Valve II has flow rate=0; tunnels lead to valves AA, JJ
            Valve JJ has flow rate=21; tunnel leads to valve II
            """;

    private static Day16.Network example;
    private static Day16.Network puzzle;
    private static HashMap<String, Integer> exampleCache;

    @BeforeAll
    public static void prepare() throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            example = Day16.parseInput(reader);
            exampleCache = new HashMap<>();
        }
        try (var reader = Day16.getPuzzleInput()) {
            puzzle = Day16.parseInput(reader);
        }
    }

    @Test
    @Disabled
    public void testExamplePart1() throws IOException {
        var result = example.findMostPressurePossibleToRelease("AA", 30);
        assertEquals(1651, result);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            24, 'CC', 'BB,DD,EE,HH,JJ', 79, 81, 1639, 1651
            21, 'EE', 'BB,DD,HH,JJ', 76, 79, 1612, 1651
            17, 'HH', 'BB,DD,JJ', 54, 76, 1326, 1651
            9, 'JJ', 'BB,DD', 33, 54, 885, 1651
            5, 'BB', 'DD', 20, 33, 560, 1651
            2, 'DD', '', 0, 20, 0, 1651
            1, 'AA', '', 0, 0, 0, 1651
            """)
    public void testExamplePart1WithCache(int minute, String valve, String open, int before, int after,
            int pressureReleased,
            int expectedResult) {
        var remainingMinutes = 30 - minute;
        var openValves = example.computeOpenValves(open);
        var result = example.findMostPressurePossibleToRelease(pressureReleased, valve, remainingMinutes, openValves,
                exampleCache);
        assertEquals(expectedResult, result);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1, 'AA', '', 0, 0, 0, 1653
            """)
    public void testPuzzleInputPart1(int minute, String valve, String open, int before, int after, int pressureReleased,
            int expectedResult) {
        var remainingMinutes = 30 - minute;
        var openValves = puzzle.computeOpenValves(open);
        var result = puzzle.findMostPressurePossibleToRelease(pressureReleased, valve, remainingMinutes, openValves,
                new HashMap<>());
        assertEquals(expectedResult, result);
    }
}

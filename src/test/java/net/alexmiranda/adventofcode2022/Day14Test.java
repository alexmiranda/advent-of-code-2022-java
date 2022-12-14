package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

public class Day14Test {
    private static final String EXAMPLE = """
            498,4 -> 498,6 -> 496,6
            503,4 -> 502,4 -> 502,9 -> 494,9
            """;

    @Test
    public void testExamplePart1() throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            var result = Day14.countSettledSandCapacity(reader);
            assertEquals(24, result);
        }
    }

    @Test
    public void testPuzzleInputPart1() throws IOException {
        var result = Day14.countSettledSandCapacity();
        assertEquals(1061, result);
    }

    @Test
    public void testAddPath() throws IOException {
        var expectedResult = """
            ..........
            ..........
            ..........
            ..........
            ....#...##
            ....#...#.
            ..###...#.
            ........#.
            ........#.
            #########.
            """;
        try (var reader = new StringReader(EXAMPLE)) {
            var reservoir = Day14.initialize(reader);
            var w = new StringWriter();
            reservoir.print(w, 10, 10);
            assertEquals(expectedResult, w.toString());
        }
    }
}

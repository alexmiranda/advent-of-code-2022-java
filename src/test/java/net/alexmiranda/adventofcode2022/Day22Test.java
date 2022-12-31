package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class Day22Test {
    private static final String EXAMPLE = """
                    ...#
                    .#..
                    #...
                    ....
            ...#.......#
            ........#...
            ..#....#....
            ..........#.
                    ...#....
                    .....#..
                    .#......
                    ......#.

            10R5L5R10L4R5L5
            """;

    @Test
    public void testExamplePart1() {
        try (var reader = new StringReader(EXAMPLE)) {
            var map = new Day22.MonkeyMap(reader);
            assertEquals(6032, map.crackPassword());
        }
    }

    @Test
    public void testPuzzleInputPart1() throws IOException {
        try (var reader = Day22.puzzleInput()) {
            var map = new Day22.MonkeyMap(reader);
            assertEquals(1428, map.crackPassword());
        }
    }
}

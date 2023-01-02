package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

public class Day23Test {
    private static final String EXAMPLE = """
            ....#..
            ..###.#
            #...#.#
            .#...##
            #.###..
            ##.#.##
            .#..#..
            """;

    private static final String SMALL_EXAMPLE = """
            .....
            ..##.
            ..#..
            .....
            ..##.
            .....
            """;

    @Test
    public void testExamplePart1() {
        try (var reader = new StringReader(EXAMPLE)) {
            var grove = new Day23.Grove(reader);
            assertEquals(110, grove.countEmptyTiles(10));
        }
    }

    @Test
    public void testExamplePart2() throws IOException {
        try (var reader = Day23.puzzleInput()) {
            var grove = new Day23.Grove(reader);
            assertEquals(3940, grove.countEmptyTiles(10));
        }
    }

    @Test
    public void testSmallExamplePart1() throws IOException {
        try (var reader = new StringReader(SMALL_EXAMPLE)) {
            var grove = new Day23.Grove(reader);
            grove.countEmptyTiles(3);
            var expectedOutput = """
                    ..#..
                    ....#
                    #....
                    ....#
                    .....
                    ..#..
                    """;
            var w = new StringWriter();
            grove.print(w);
            assertEquals(expectedOutput, w.toString());
        }
    }
}

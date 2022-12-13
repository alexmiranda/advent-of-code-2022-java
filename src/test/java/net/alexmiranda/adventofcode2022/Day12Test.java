package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

public class Day12Test {
    private static final String EXAMPLE = """
            Sabqponm
            abcryxxl
            accszExk
            acctuvwj
            abdefghi
            """;

    @Test
    public void testExamplePart1() {
        var heightmap = Day12.fromString(EXAMPLE);
        assertEquals(31, Day12.shortestDistance(heightmap));
    }

    @Test
    public void testPuzzleInputPart1() throws URISyntaxException, IOException {
        var heightmap = Day12.readInputFile();
        assertEquals(484, Day12.shortestDistance(heightmap));
    }
}

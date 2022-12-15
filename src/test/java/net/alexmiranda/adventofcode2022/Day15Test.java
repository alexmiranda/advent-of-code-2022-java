package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class Day15Test {
    private static final String EXAMPLE = """
            Sensor at x=2, y=18: closest beacon is at x=-2, y=15
            Sensor at x=9, y=16: closest beacon is at x=10, y=16
            Sensor at x=13, y=2: closest beacon is at x=15, y=3
            Sensor at x=12, y=14: closest beacon is at x=10, y=16
            Sensor at x=10, y=20: closest beacon is at x=10, y=16
            Sensor at x=14, y=17: closest beacon is at x=10, y=16
            Sensor at x=8, y=7: closest beacon is at x=2, y=10
            Sensor at x=2, y=0: closest beacon is at x=2, y=10
            Sensor at x=0, y=11: closest beacon is at x=2, y=10
            Sensor at x=20, y=14: closest beacon is at x=25, y=17
            Sensor at x=17, y=20: closest beacon is at x=21, y=22
            Sensor at x=16, y=7: closest beacon is at x=15, y=3
            Sensor at x=14, y=3: closest beacon is at x=15, y=3
            Sensor at x=20, y=1: closest beacon is at x=15, y=3
            """;

    @Test
    public void testExamplePart1() throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            var result = Day15.countLocationsUnsuitableForBeacons(reader, 10);
            assertEquals(26, result);
        }
    }

    @Test
    public void testPuzzleInputPart1() throws IOException {
        var result = Day15.countLocationsUnsuitableForBeacons(2_000_000);
        assertEquals(5367037, result);
    }

    @Test
    public void testExamplePart2() throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            var result = Day15.getTunningFrequency(reader);
            assertEquals(56000011, result);
        }
    }

    @Test
    public void testPuzzleInputPart2() throws IOException {
        var result = Day15.getTunningFrequency();
        assertEquals(11914583249288L, result);
    }
}

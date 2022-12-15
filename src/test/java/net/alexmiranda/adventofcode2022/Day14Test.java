package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

public class Day14Test {
    @Test
    public void testExamplePart1() throws IOException {
        try (var reader = new StringReader(Day14.EXAMPLE)) {
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
    public void testExamplePart2() throws IOException {
        try (var reader = new StringReader(Day14.EXAMPLE)) {
            var result = Day14.countSettledSandCapacityRevised(reader);
            assertEquals(93, result);
        }
    }

    @Test
    public void testPuzzleInputPart2() throws IOException {
        var result = Day14.countSettledSandCapacityRevised();
        assertEquals(25055, result);
    }

    @Test
    public void testAddPath() throws IOException {
        var expectedResult = """
                ......+...
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
        try (var reader = new StringReader(Day14.EXAMPLE)) {
            var reservoir = Day14.initialize(new Day14.RegolithReservoir(), reader);
            var w = new StringWriter();
            reservoir.print(w, 10, 10);
            assertEquals(expectedResult, w.toString());
        }
    }

    @Test
    public void testPrintRevised() throws IOException {
        var expectedResult = """
                ..........+..........
                .........ooo.........
                ........ooooo........
                .......ooooooo.......
                ......oo#ooo##o......
                .....ooo#ooo#ooo.....
                ....oo###ooo#oooo....
                ...oooo.oooo#ooooo...
                ..oooooooooo#oooooo..
                .ooo#########ooooooo.
                ooooo.......ooooooooo
                """;
        try (var reader = new StringReader(Day14.EXAMPLE)) {
            var reservoir = Day14.initialize(new Day14.RegolithReservoirRevised(), reader);
            reservoir.simulate(Day14.Point.SOURCE_OF_SAND);
            var w = new StringWriter();
            reservoir.print(w, 11, 21);
            assertEquals(expectedResult, w.toString());
        }
    }
}

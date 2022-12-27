package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class Day17Test {
    private static final String EXAMPLE = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>";
    private static final String INPUT = "2022/Day/17/input";

    @Test
    public void testExamplePart1() {
        var chamber = new Day17.Chamber(EXAMPLE);
        var result = chamber.simulate(2022);
        assertEquals(3068, result);
    }

    @Test
    public void testPuzzleInputPart1() throws IOException, URISyntaxException {
        var input = Files.readString(Path.of(ClassLoader.getSystemResource(INPUT).toURI()));
        var chamber = new Day17.Chamber(input);
        var result = chamber.simulate(2022);
        assertEquals(3059, result);
        assertEquals(10091, input.length());
    }

    @Test
    @Disabled("takes way too long")
    public void testExamplePart2() {
        var chamber = new Day17.Chamber(EXAMPLE);
        var result = chamber.simulate(1_000_000_000_000L);
        assertEquals(1_514_285_714_288L, result);
    }

    @Test
    public void testPuzzleInputPart2() throws IOException, URISyntaxException {
        var input = Files.readString(Path.of(ClassLoader.getSystemResource(INPUT).toURI()));
        var chamber = new Day17.Chamber(input);
        var result = chamber.simulate(1_000_000_000_000L);
        assertEquals(1500874635587L, result);
    }

    @Test
    public void testPrintExample() throws IOException {
        var chamber = new Day17.Chamber(EXAMPLE);
        var w = new StringWriter();
        var expected = """
                |..@@@@.|
                |.......|
                |.......|
                |.......|
                +-------+
                """;
        chamber.print(w);
        assertEquals(expected, w.toString());
    }

    @Test
    public void testPrintExample1() throws IOException {
        var chamber = new Day17.Chamber(EXAMPLE);
        chamber.simulate(1);
        var w = new StringWriter();
        var expected = """
                |...@...|
                |..@@@..|
                |...@...|
                |.......|
                |.......|
                |.......|
                |..####.|
                +-------+
                """;
        chamber.print(w);
        assertEquals(expected, w.toString());
    }

    @Test
    public void testPrintExample2() throws IOException {
        var chamber = new Day17.Chamber(EXAMPLE);
        chamber.simulate(2);
        var w = new StringWriter();
        var expected = """
                |....@..|
                |....@..|
                |..@@@..|
                |.......|
                |.......|
                |.......|
                |...#...|
                |..###..|
                |...#...|
                |..####.|
                +-------+
                """;
        chamber.print(w);
        assertEquals(expected, w.toString());
    }

    @Test
    public void testPrintExample3() throws IOException {
        var chamber = new Day17.Chamber(EXAMPLE);
        chamber.simulate(3);
        var w = new StringWriter();
        var expected = """
                |..@....|
                |..@....|
                |..@....|
                |..@....|
                |.......|
                |.......|
                |.......|
                |..#....|
                |..#....|
                |####...|
                |..###..|
                |...#...|
                |..####.|
                +-------+
                """;
        chamber.print(w);
        assertEquals(expected, w.toString());
    }

    @Test
    public void testPrintExample4() throws IOException {
        var chamber = new Day17.Chamber(EXAMPLE);
        chamber.simulate(4);
        var w = new StringWriter();
        var expected = """
                |..@@...|
                |..@@...|
                |.......|
                |.......|
                |.......|
                |....#..|
                |..#.#..|
                |..#.#..|
                |#####..|
                |..###..|
                |...#...|
                |..####.|
                +-------+
                """;
        chamber.print(w);
        assertEquals(expected, w.toString());
    }

    @Test
    public void testPrintExample5() throws IOException {
        var chamber = new Day17.Chamber(EXAMPLE);
        chamber.simulate(5);
        var w = new StringWriter();
        var expected = """
                |..@@@@.|
                |.......|
                |.......|
                |.......|
                |....##.|
                |....##.|
                |....#..|
                |..#.#..|
                |..#.#..|
                |#####..|
                |..###..|
                |...#...|
                |..####.|
                +-------+
                """;
        chamber.print(w);
        assertEquals(expected, w.toString());
    }

    @Test
    public void testMoveAndPrintShapes() throws IOException {
        try (var w = new StringWriter()) {
            Day17.printRock(w, Day17.HORIZONTAL);
            var s = """
                    |..@@@@.|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.HORIZONTAL;
            shape = Day17.moveRock(shape, 0, '<');
            Day17.printRock(w, shape);
            var s = """
                    |.@@@@..|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.HORIZONTAL;
            shape = Day17.moveRock(shape, 0, '<');
            shape = Day17.moveRock(shape, 0, '<');
            Day17.printRock(w, shape);
            var s = """
                    |@@@@...|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.HORIZONTAL;
            shape = Day17.moveRock(shape, 0, '<');
            shape = Day17.moveRock(shape, 0, '<');
            shape = Day17.moveRock(shape, 0, '<'); // noop
            Day17.printRock(w, shape);
            var s = """
                    |@@@@...|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.HORIZONTAL;
            shape = Day17.moveRock(shape, 0, '>');
            Day17.printRock(w, shape);
            var s = """
                    |...@@@@|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.HORIZONTAL;
            shape = Day17.moveRock(shape, 0, '>');
            shape = Day17.moveRock(shape, 0, '>'); // noop
            Day17.printRock(w, shape);
            var s = """
                    |...@@@@|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            Day17.printRock(w, Day17.CROSS);
            var s = """
                    |...@...|
                    |..@@@..|
                    |...@...|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.CROSS;
            shape = Day17.moveRock(shape, 0, '<');
            shape = Day17.moveRock(shape, 0, '<');
            shape = Day17.moveRock(shape, 0, '<'); // noop
            Day17.printRock(w, shape);
            var s = """
                    |.@.....|
                    |@@@....|
                    |.@.....|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.CROSS;
            shape = Day17.moveRock(shape, 0, '>');
            shape = Day17.moveRock(shape, 0, '>');
            shape = Day17.moveRock(shape, 0, '>'); // noop
            Day17.printRock(w, shape);
            var s = """
                    |.....@.|
                    |....@@@|
                    |.....@.|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            Day17.printRock(w, Day17.LSHAPE);
            var s = """
                    |....@..|
                    |....@..|
                    |..@@@..|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.LSHAPE;
            shape = Day17.moveRock(shape, 0, '<');
            shape = Day17.moveRock(shape, 0, '<');
            shape = Day17.moveRock(shape, 0, '<'); // noop
            Day17.printRock(w, shape);
            var s = """
                    |..@....|
                    |..@....|
                    |@@@....|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.LSHAPE;
            shape = Day17.moveRock(shape, 0, '>');
            shape = Day17.moveRock(shape, 0, '>');
            shape = Day17.moveRock(shape, 0, '>'); // noop
            Day17.printRock(w, shape);
            var s = """
                    |......@|
                    |......@|
                    |....@@@|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            Day17.printRock(w, Day17.VERTICAL);
            var s = """
                    |..@....|
                    |..@....|
                    |..@....|
                    |..@....|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.VERTICAL;
            shape = Day17.moveRock(shape, 0, '<');
            shape = Day17.moveRock(shape, 0, '<');
            shape = Day17.moveRock(shape, 0, '<'); // noop
            Day17.printRock(w, shape);
            var s = """
                    |@......|
                    |@......|
                    |@......|
                    |@......|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.VERTICAL;
            shape = Day17.moveRock(shape, 0, '>');
            shape = Day17.moveRock(shape, 0, '>');
            shape = Day17.moveRock(shape, 0, '>');
            shape = Day17.moveRock(shape, 0, '>'); // noop
            Day17.printRock(w, shape);
            var s = """
                    |......@|
                    |......@|
                    |......@|
                    |......@|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            Day17.printRock(w, Day17.SQUARE);
            var s = """
                    |..@@...|
                    |..@@...|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.SQUARE;
            shape = Day17.moveRock(shape, 0, '<');
            shape = Day17.moveRock(shape, 0, '<');
            shape = Day17.moveRock(shape, 0, '<'); // noop
            Day17.printRock(w, shape);
            var s = """
                    |@@.....|
                    |@@.....|
                    """;
            assertEquals(s, w.toString());
        }

        try (var w = new StringWriter()) {
            var shape = Day17.SQUARE;
            shape = Day17.moveRock(shape, 0, '>');
            shape = Day17.moveRock(shape, 0, '>');
            shape = Day17.moveRock(shape, 0, '>');
            shape = Day17.moveRock(shape, 0, '>'); // noop
            Day17.printRock(w, shape);
            var s = """
                    |.....@@|
                    |.....@@|
                    """;
            assertEquals(s, w.toString());
        }
    }

    @Test
    public void testShapeHeight() {
        assertEquals(1, Day17.height(Day17.HORIZONTAL));
        assertEquals(3, Day17.height(Day17.CROSS));
        assertEquals(3, Day17.height(Day17.LSHAPE));
        assertEquals(2, Day17.height(Day17.SQUARE));
        assertEquals(4, Day17.height(Day17.VERTICAL));
    }
}

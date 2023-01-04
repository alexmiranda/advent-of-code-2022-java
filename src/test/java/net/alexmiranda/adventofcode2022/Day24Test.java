package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class Day24Test {
    private static final String SIMPLE_EXAMPLE = """
            #.#####
            #.....#
            #>....#
            #.....#
            #...v.#
            #.....#
            #####.#
            """;

    private static final String COMPLEX_EXAMPLE = """
            #.######
            #>>.<^<#
            #.<..<<#
            #>v.><>#
            #<^v^^>#
            ######.#
            """;

    @Test
    public void testSimpleExamplePart1() throws IOException {
        try (var reader = new StringReader(SIMPLE_EXAMPLE)) {
            var valley = new Day24.Valley(reader);
            var result = valley.shortestTime();
            assertEquals(10, result);
        }
    }

    @Test
    public void testComplexExamplePart1() throws IOException {
        try (var reader = new StringReader(COMPLEX_EXAMPLE)) {
            var valley = new Day24.Valley(reader);
            var result = valley.shortestTime();
            assertEquals(18, result);
        }
    }

    @Test
    public void testPuzzleInputPart1() throws IOException {
        try (var reader = Day24.puzzleInput()) {
            var valley = new Day24.Valley(reader);
            var result = valley.shortestTime();
            assertEquals(343, result);
        }
    }

    @Test
    public void testSimpleExamplePart2() {
        try (var reader = new StringReader(SIMPLE_EXAMPLE)) {
            var valley = new Day24.Valley(reader);
            assertEquals(30, valley.shortestTimeRounds(3));
        }
    }

    @Test
    public void testComplexExamplePart2() {
        try (var reader = new StringReader(COMPLEX_EXAMPLE)) {
            var valley = new Day24.Valley(reader);
            var timeElapsed = 0;

            // assertEquals(54, valley.shortestTimeRounds(3));

            // reach the goal
            var minutes = valley.shortestTime(timeElapsed, valley.startPos, valley.finishPos);
            assertEquals(18, minutes);
            timeElapsed += minutes;

            // go back to start
            minutes = valley.shortestTime(timeElapsed, valley.finishPos, valley.startPos);
            assertEquals(23, minutes);
            timeElapsed += minutes;

            // and reach the goal again...
            minutes = valley.shortestTime(timeElapsed, valley.startPos, valley.finishPos);
            assertEquals(13, minutes);
            timeElapsed += minutes;

            assertEquals(54, timeElapsed);
        }
    }

    @Test
    public void testPuzzleInputPart2() throws IOException {
        try (var reader = Day24.puzzleInput()) {
            var valley = new Day24.Valley(reader);
            var timeElapsed = 0;
            timeElapsed += valley.shortestTime(timeElapsed, valley.startPos, valley.finishPos);
            timeElapsed += valley.shortestTime(timeElapsed, valley.finishPos, valley.startPos);
            timeElapsed += valley.shortestTime(timeElapsed, valley.startPos, valley.finishPos);
            assertEquals(960, timeElapsed);
        }
    }

    @Test
    public void testPuzzleInputMoveBlizzards() throws IOException {
        try (var reader = Day24.puzzleInput()) {
            var valley = new Day24.Valley(reader);
            var data = valley.data();
            var width = valley.width();
            
            // before
            var w1 = new StringWriter();
            Day24.Valley.print(w1, data, width);

            for (int i = 0; i < 300; i++) {
                data = valley.moveBlizzards(data);
            }

            // after
            var w2 = new StringWriter();
            Day24.Valley.print(w2, data, width);
            assertEquals(w1.toString(), w2.toString());
        }
    }

    @Test
    public void testReadAndPrint() throws IOException {
        try (var reader = new StringReader(COMPLEX_EXAMPLE)) {
            var valley = new Day24.Valley(reader);
            var w = new StringWriter();
            valley.print(w);
            assertEquals(COMPLEX_EXAMPLE, w.toString());
        }
    }

    @Test
    public void testReadAndPrintPuzzleInput() throws IOException {
        try (var reader = Day24.puzzleInput()) {
            var contents = (new BufferedReader(reader)).lines().collect(Collectors.joining("\n"));
            contents += "\n";
            var valley = new Day24.Valley(new StringReader(contents));
            var w = new StringWriter();
            valley.print(w);
            assertEquals(contents, w.toString());
        }
    }

    @Test
    public void testMoveBlizzards() throws IOException {
        try (var reader = new StringReader(COMPLEX_EXAMPLE)) {
            var valley = new Day24.Valley(reader);
            var data = valley.data();
            var width = valley.width();
            var w = new StringWriter();

            for (int i = 0; i < 18; i++) {
                data = valley.moveBlizzards(data);
                Day24.Valley.print(w, data, width);
                w.write('\n');
            }

            var expectedString = """
                    #.######
                    #.>3.<.#
                    #<..<<.#
                    #>2.22.#
                    #>v..^<#
                    ######.#

                    #.######
                    #.2>2..#
                    #.^22^<#
                    #.>2.^>#
                    #.>..<.#
                    ######.#

                    #.######
                    #<^<22.#
                    #.2<.2.#
                    #><2>..#
                    #..><..#
                    ######.#

                    #.######
                    #.<..22#
                    #<<.<..#
                    #<2.>>.#
                    #.^22^.#
                    ######.#

                    #.######
                    #2.v.<>#
                    #<.<..<#
                    #.^>^22#
                    #.2..2.#
                    ######.#

                    #.######
                    #>2.<.<#
                    #.2v^2<#
                    #>..>2>#
                    #<....>#
                    ######.#

                    #.######
                    #.22^2.#
                    #<v.<2.#
                    #>>v<>.#
                    #>....<#
                    ######.#

                    #.######
                    #.<>2^.#
                    #..<<.<#
                    #.22..>#
                    #.2v^2.#
                    ######.#

                    #.######
                    #<.2>>.#
                    #.<<.<.#
                    #>2>2^.#
                    #.v><^.#
                    ######.#

                    #.######
                    #.2..>2#
                    #<2v2^.#
                    #<>.>2.#
                    #..<>..#
                    ######.#

                    #.######
                    #2^.^2>#
                    #<v<.^<#
                    #..2.>2#
                    #.<..>.#
                    ######.#

                    #.######
                    #>>.<^<#
                    #.<..<<#
                    #>v.><>#
                    #<^v^^>#
                    ######.#

                    #.######
                    #.>3.<.#
                    #<..<<.#
                    #>2.22.#
                    #>v..^<#
                    ######.#

                    #.######
                    #.2>2..#
                    #.^22^<#
                    #.>2.^>#
                    #.>..<.#
                    ######.#

                    #.######
                    #<^<22.#
                    #.2<.2.#
                    #><2>..#
                    #..><..#
                    ######.#

                    #.######
                    #.<..22#
                    #<<.<..#
                    #<2.>>.#
                    #.^22^.#
                    ######.#

                    #.######
                    #2.v.<>#
                    #<.<..<#
                    #.^>^22#
                    #.2..2.#
                    ######.#

                    #.######
                    #>2.<.<#
                    #.2v^2<#
                    #>..>2>#
                    #<....>#
                    ######.#

                    """;
            assertEquals(expectedString, w.toString());
        }
    }

}

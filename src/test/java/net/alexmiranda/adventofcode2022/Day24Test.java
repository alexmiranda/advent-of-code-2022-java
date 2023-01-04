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

            var expectedString = """
                    #.######
                    #>2.<.<#
                    #.2v^2<#
                    #>..>2>#
                    #<....>#
                    ######.#
                    """;

            var w = new StringWriter();
            valley.print(w);
            assertEquals(expectedString, w.toString());
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
    public void testPuzzleInputMoveBlizzards() throws IOException {
        try (var reader = Day24.puzzleInput()) {
            var valley = new Day24.Valley(reader);
            for (int i = 0; i < 300; i++) {
                valley.moveBlizzards();
            }
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
            var w = new StringWriter();

            for (int i = 0; i < 18; i++) {
                valley.moveBlizzards();
                valley.print(w);
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

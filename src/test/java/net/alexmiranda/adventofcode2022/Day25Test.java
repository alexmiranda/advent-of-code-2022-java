package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class Day25Test {
    private static final String EXAMPLE = """
            1=-0-2
            12111
            2=0=
            21
            2=01
            111
            20012
            112
            1=-1=
            1-12
            12
            1=
            122
            """;

    @ParameterizedTest
    @CsvSource(textBlock = """
            '1', 1
            '2', 2
            '1=', 3
            '1-', 4
            '10', 5
            '11', 6
            '12', 7
            '2=', 8
            '2-', 9
            '20', 10
            '1=0', 15
            '1-0', 20
            '1=11-2', 2022
            '1-0---0', 12345
            '1121-1110-1=0', 314159265
            '1=-0-2', 1747
            '12111', 906
            '2=0=', 198
            '21', 11
            '2=01', 201
            '111', 31
            '20012', 1257
            '112', 32
            '1=-1=', 353
            '1-12', 107
            '12', 7
            '1=', 3
            '122', 37
            """)
    public void testSNAFUConversions(String s, int i) {
        var snafu = Day25.SNAFU.fromString(s);
        assertEquals(i, snafu.longValue());
        assertEquals(s, Day25.SNAFU.fromInt(i).toString());
    }

    @Test
    public void testExamplePart1() {
        try (var reader = new StringReader(EXAMPLE)) {
            var sum = Day25.sum(reader);
            assertEquals(4890, sum.longValue());
            assertEquals("2=-1=0", sum.toString());
        }
    }

    @Test
    public void testPuzzleInputPart1() throws IOException {
        try (var reader = Day25.puzzleInput()) {
            var sum = Day25.sum(reader);
            assertEquals(28127432121050L, sum.longValue());
            assertEquals("122-2=200-0111--=200", sum.toString());
            assertEquals(28127432121050L, Day25.SNAFU.fromString(sum.toString()).longValue());
        }
    }
}

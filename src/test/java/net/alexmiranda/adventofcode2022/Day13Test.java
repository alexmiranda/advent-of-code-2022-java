package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import net.alexmiranda.adventofcode2022.Day13.Packet;

public class Day13Test {
    private static final String EXAMPLE = """
            [1,1,3,1,1]
            [1,1,5,1,1]

            [[1],[2,3,4]]
            [[1],4]

            [9]
            [[8,7,6]]

            [[4,4],4,4]
            [[4,4],4,4,4]

            [7,7,7,7]
            [7,7,7]

            []
            [3]

            [[[]]]
            [[]]

            [1,[2,[3,[4,[5,6,7]]]],8,9]
            [1,[2,[3,[4,[5,6,0]]]],8,9]
            """;

    @Test
    public void canParseEveryLineOfExample() {
        canParseLines(Stream.of(EXAMPLE.split("\n")));
    }

    @Test
    public void canParseEveryLineOfPuzzleInput() throws IOException {
        try (var br = new BufferedReader(Day13.readInput())) {
            canParseLines(br.lines());
        }
    }

    private void canParseLines(Stream<String> lines) {
        lines.filter(s -> !s.isEmpty()).forEach((line) -> {
            Packet packet = Day13.Packet.fromString(line);
            assertNotNull(packet);
            assertEquals(line, packet.toString());
        });
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        '[1,1,3,1,1]', '[1,1,5,1,1]', true
        '[[1],[2,3,4]]', '[[1],4]', true
        '[9]', '[[8,7,6]]', false
        '[[4,4],4,4]', '[[4,4],4,4,4]', true
        '[7,7,7,7]', '[7,7,7]', false
        '[]', '[3]', true
        '[[[]]]', '[]', false
        '[1,[2,[3,[4,[5,6,7]]]],8,9]', '[1,[2,[3,[4,[5,6,0]]]],8,9]', false
    """)
    public void testPairComparison(String a, String b, boolean expectedResult) {
        var left = Day13.Packet.fromString(a);
        var right = Day13.Packet.fromString(b);
        var cmp = left.compareTo(right);
        assertEquals(expectedResult, cmp <= 0);
    }

    @Test
    public void testExamplePart1() throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            assertEquals(13, Day13.countPairsInRightOrder(reader));
        }
    }
    
    @Test
    public void testPuzzleInputPart1() throws IOException {
        assertEquals(6072, Day13.countPairsInRightOrder());
    }

    @Test
    public void testExamplePart2() throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            var result = Day13.decoderKeyOfDistressSignal(reader, Day13.Packet.fromString("[[2]]"), Day13.Packet.fromString("[[6]]"));
            assertEquals(140, result);
        }
    }

    @Test
    public void testPuzzleInputPart2() throws IOException {
        var result = Day13.decoderKeyOfDistressSignal(Day13.Packet.fromString("[[2]]"), Day13.Packet.fromString("[[6]]"));
        assertEquals(22184, result);
    }
}

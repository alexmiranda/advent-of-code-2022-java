package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class Day11Test {
    private static final String INPUT = "/2022/Day/11/input";

    private static final String example = """
            Monkey 0:
            Starting items: 79, 98
            Operation: new = old * 19
            Test: divisible by 23
                If true: throw to monkey 2
                If false: throw to monkey 3

            Monkey 1:
            Starting items: 54, 65, 75, 74
            Operation: new = old + 6
            Test: divisible by 19
                If true: throw to monkey 2
                If false: throw to monkey 0

            Monkey 2:
            Starting items: 79, 60, 97
            Operation: new = old * old
            Test: divisible by 13
                If true: throw to monkey 1
                If false: throw to monkey 3

            Monkey 3:
            Starting items: 74
            Operation: new = old + 3
            Test: divisible by 17
                If true: throw to monkey 0
                If false: throw to monkey 1

            """;

    @Test
    public void testExamplePart1() throws IOException {
        try (var reader = new StringReader(example)) {
            var game = new Day11.KeepAway(reader, 3);
            game.play(20);
            assertEquals(BigInteger.valueOf(10605), game.monkeyBusiness());
        }
    }

    @Test
    public void testPuzzleInputPart1() throws IOException {
        try (var reader = new InputStreamReader(Day11.class.getResourceAsStream(INPUT))) {
            var game = new Day11.KeepAway(reader, 3);
            game.play(20);
            assertEquals(BigInteger.valueOf(101436), game.monkeyBusiness());
        }
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
                1, '24'
                20, '10197'
                1000, '28440885'
                2000, '114468597'
                10000, '2876069637'
            """)
    public void testExamplePart2(int rounds, String result) throws IOException {
        try (var reader = new StringReader(example)) {
            var game = new Day11.KeepAway(reader, 1);
            game.play(rounds);
            assertEquals(new BigInteger(result), game.monkeyBusiness());
        }
    }

    @Test
    public void testPuzzleInputPart2() throws IOException {
        try (var reader = new InputStreamReader(Day11.class.getResourceAsStream(INPUT))) {
            var game = new Day11.KeepAway(reader, 1);
            game.play(10_000);
            assertEquals(new BigInteger("29396129184"), game.monkeyBusiness());
        }
    }
}

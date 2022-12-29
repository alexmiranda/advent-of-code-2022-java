package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class Day21Test {
    private static final String EXAMPLE = """
            root: pppw + sjmn
            dbpl: 5
            cczh: sllz + lgvd
            zczc: 2
            ptdq: humn - dvpt
            dvpt: 3
            lfqf: 4
            humn: 5
            ljgn: 2
            sjmn: drzm * dbpl
            sllz: 4
            pppw: cczh / lfqf
            lgvd: ljgn * ptdq
            drzm: hmdt - zczc
            hmdt: 32
            """;

    @Test
    public void testExamplePart1() throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            var ctx = new Day21.Context(reader, false);
            var answer = ctx.solve("root");
            assertEquals(152L, answer);
        }
    }

    @Test
    public void testPuzzleInputPart1() throws IOException {
        try (var reader = Day21.puzzleInput()) {
            var ctx = new Day21.Context(reader, false);
            var answer = ctx.solve("root");
            assertEquals(282285213953670L, answer);
        }
    }

    @Test
    public void testExamplePart2() throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            var ctx = new Day21.Context(reader, true);
            var answer = ctx.solvePart2("humn");
            assertEquals(301L, answer);
        }
    }

    @Test
    public void testExamplePart2CleverWay() throws IOException {
        try (var reader = new StringReader(EXAMPLE)) {
            var ctx = new Day21.Context(reader, true, true);
            var answer = ctx.solvePart2CleverWay("humn");
            assertEquals(301L, answer);
        }
    }

    @Test
    public void testPuzzleInputPart2() throws IOException {
        try (var reader = Day21.puzzleInput()) {
            var ctx = new Day21.Context(reader, true, true);
            var answer = ctx.solvePart2CleverWay("humn");
            assertEquals(3699945358564L, answer);
        }
    }
}

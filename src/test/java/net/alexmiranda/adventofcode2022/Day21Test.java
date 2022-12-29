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
            var ctx = new Day21.Context(reader);
            var answer = ctx.solve("root");
            assertEquals("152", answer);
        }
    }

    @Test
    public void testPuzzleInputPart1() throws IOException {
        try (var reader = Day21.puzzleInput()) {
            var ctx = new Day21.Context(reader);
            var answer = ctx.solve("root");
            assertEquals("282285213953670", answer);
        }
    }
}

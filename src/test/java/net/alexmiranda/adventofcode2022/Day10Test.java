package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class Day10Test {
    private static final String EXAMPLE = "/2022/Day/10/example";
    private static final String INPUT = "/2022/Day/10/input";

    @Test
    public void testExamplePart1() {
        checkPart1(example(), 13140);
    }

    @Test
    public void testPuzzleInputPart1() {
        checkPart1(input(), 13820);
    }

    public void checkPart1(Reader reader, int expectedResult) {
        try (var clockCircuit = new Day10.ClockCircuit(reader)) {
            var signals = new HashMap<Integer, Integer>(6);
            clockCircuit.setListener(cycle -> {
                switch (cycle) {
                    case 20, 60, 100, 140, 180, 220 -> {
                        signals.put(cycle, clockCircuit.signalStrength());
                    }
                }
            });
            clockCircuit.ready();
            assertEquals(expectedResult, sum(signals));
        }
    }

    private InputStreamReader example() {
        return new InputStreamReader(Day10.class.getResourceAsStream(EXAMPLE));
    }

    private InputStreamReader input() {
        return new InputStreamReader(Day10.class.getResourceAsStream(INPUT));
    }

    int sum(Map<?, Integer> signals) {
        return signals.values().stream().mapToInt(Integer::valueOf).sum();
    }
}

package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class Day20Test {
    private static final int NO_DECRYPTION_KEY = 1;

    @Test
    public void testExamplePart1() {
        var file = Arrays.asList(1L, 2L, -3L, 3L, -2L, 0L, 4L);
        // var expected = Arrays.asList(1, 2, -3, 4, 0, 3, -2);
        var mixed = Day20.mix(file, 1);
        // assertIterableEquals(expected, mixed);
        assertEquals(3, Day20.sumOfGroveCoordinates(mixed));
    }

    @Test
    public void testPuzzleInputPart1() throws IOException {
        var file = Day20.puzzleInput();
        var mixed = Day20.mix(file, 5000, 1, NO_DECRYPTION_KEY);
        assertEquals(988, Day20.sumOfGroveCoordinates(mixed));
    }

    @Test
    public void testExamplePart2() {
        var file = Day20.applyDecryptionKey(Arrays.asList(1L, 2L, -3L, 3L, -2L, 0L, 4L));
        var mixed = Day20.mix(file, 10);
        assertEquals(1623178306L, Day20.sumOfGroveCoordinates(mixed));
    }

    @Test
    public void testPuzzleInputPart2() throws IOException {
        var file = Day20.puzzleInput();
        var mixed = Day20.mix(file, 5000, 10, Day20.DECRYPTION_KEY);
        assertEquals(7768531372516L, Day20.sumOfGroveCoordinates(mixed));
    }
}

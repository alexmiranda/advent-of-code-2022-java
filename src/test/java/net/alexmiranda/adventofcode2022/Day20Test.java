package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class Day20Test {
    @Test
    public void testExamplePart1() {
        var file = Arrays.asList(1, 2, -3, 3, -2, 0, 4);
        // var expected = Arrays.asList(1, 2, -3, 4, 0, 3, -2);
        var mixed = Day20.mix(file);
        // assertIterableEquals(expected, mixed);
        assertEquals(3, Day20.answerPart1(mixed));
    }

    @Test
    public void testPuzzleInputPart1() throws IOException {
        var file = Day20.puzzleInput();
        var mixed = Day20.mix(file, 5000);
        assertEquals(988, Day20.answerPart1(mixed));
    }
}

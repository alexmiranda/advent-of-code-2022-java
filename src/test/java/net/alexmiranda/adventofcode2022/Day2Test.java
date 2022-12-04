package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

public class Day2Test {

    @Test
    public void testCalculateScore() throws URISyntaxException, IOException {
        assertEquals(17189, Day2.totalScore());
    }

    @Test
    public void testCalculateScoreOutcomes() throws URISyntaxException, IOException {
        assertEquals(13490, Day2.totalScoreOutcomes());
    }
}

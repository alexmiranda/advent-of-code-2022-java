package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

public class Day1Test {
    @Test
    public void testDay1() throws URISyntaxException, IOException {
        assertEquals(71780, Day1.calorieCounting());
    }

    @Test
    public void testDay1PartTwo() throws URISyntaxException, IOException {
        assertEquals(212489, Day1.calorieCountingTopThree());
    }
}
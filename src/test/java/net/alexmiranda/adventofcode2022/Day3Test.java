package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class Day3Test {
    @Test
    public void testSumOfPriorities() throws URISyntaxException, IOException {
        assertEquals(7848, Day3.sumOfPriorities());
    }

    @Test
    public void testSumOfGroupPriorities() throws URISyntaxException, IOException {
        assertEquals(2616, Day3.sumOfGroupPriorities());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
                vJrwpWtwJgWrhcsFMMfFFhFp, 16
                jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL, 38
                PmmdzqPrVvPwwTWBwg, 42
                wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn, 22
                ttgJtRGJQctTZtZT, 20
                CrZsJsPPZsGzwwsLwLmpwMDw, 19
            """)
    public void testCommonItemsPriority(String line, int expectedResult) {
        assertEquals(expectedResult, Day3.commonItemsPriority(line));
    }

    @ParameterizedTest
    @ValueSource(strings = """
            vJrwpWtwJgWrhcsFMMfFFhFp
            jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
            PmmdzqPrVvPwwTWBwg
            """)
    public void testCommonGroupPriority(String lines) {
        assertEquals(18, Day3.commonGroupPriority(lines.split("\n")));
    }
}

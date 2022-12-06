package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class Day6Test {
    private static final String INPUT = "2022/Day/6/input";

    @ParameterizedTest
    @CsvSource(textBlock = """
            'mjqjpqmgbljsphdztnvjfqwrcgsmlb', 7
            'bvwbjplbgvbhsrlpgdmjqwftvncz', 5
            'nppdvjthqldpwncqszvftbrmjlhg', 6
            'nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg', 10
            'zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw', 11
            """)
    public void testCharactersBeforeStartOfNewPacket(String s, int expectedResult) {
        assertEquals(expectedResult, Day6.charactersBeforeStartOfNewPacket(s));
    }

    @Test
    public void testStartOfPacketInputString() throws URISyntaxException, IOException {
        var s = readInputFile();
        assertEquals(1848, Day6.charactersBeforeStartOfNewPacket(s));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            'mjqjpqmgbljsphdztnvjfqwrcgsmlb', 19
            'bvwbjplbgvbhsrlpgdmjqwftvncz', 23
            'nppdvjthqldpwncqszvftbrmjlhg', 23
            'nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg', 29
            'zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw', 26
            """)
    public void testCharactersBeforeStartOfNewMessage(String s, int expectedResult) {
        assertEquals(expectedResult, Day6.charactersBeforeStartOfNewMessage(s));
    }

    @Test
    public void testStartOfMessageInputString() throws URISyntaxException, IOException {
        var s = readInputFile();
        assertEquals(2308, Day6.charactersBeforeStartOfNewMessage(s));
    }

    private String readInputFile() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        return Files.readString(path);
    }
}

package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

public class Day7Test {
    private static final String example = """
            $ cd /
            $ ls
            dir a
            14848514 b.txt
            8504156 c.dat
            dir d
            $ cd a
            $ ls
            dir e
            29116 f
            2557 g
            62596 h.lst
            $ cd e
            $ ls
            584 i
            $ cd ..
            $ cd ..
            $ cd d
            $ ls
            4060174 j
            8033020 d.log
            5626152 d.ext
            7214296 k
            """;

    @Test
    public void testExamplePart1() throws IOException {
        var reader = new StringReader(example);
        int result = Day7.part1(reader);
        assertEquals(95437, result);
    }

    @Test
    public void testPuzzleInputPart1() throws URISyntaxException, IOException {
        int result = Day7.part1();
        assertEquals(1778099, result);
    }

    @Test
    public void testExamplePart2() throws IOException {
        var reader = new StringReader(example);
        int result = Day7.part2(reader);
        assertEquals(24933642, result);
    }

    @Test
    public void testPuzzleInputPart2() throws URISyntaxException, IOException {
        int result = Day7.part2();
        assertEquals(1623571, result);
    }
}

package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

public class Day5Test {
    private static final String sample = """
                [D]   \s
            [N] [C]   \s
            [Z] [M] [P]
             1   2   3\s
            """;

    @Test
    public void testLoad9000() throws IOException {
        var reader = new StringReader(sample);
        var crateMover = new Day5.CrateMover9000();
        crateMover.load(reader);
        assertTrue("NDP".contentEquals(crateMover.cratesOnTop()));
    }

    @Test
    public void testLoad9001() throws IOException {
        var reader = new StringReader(sample);
        var crateMover = new Day5.CrateMover9001();
        crateMover.load(reader);
        assertTrue("NDP".contentEquals(crateMover.cratesOnTop()));
    }

    @Test
    public void testRearrange9000() throws IOException, URISyntaxException {
        var crateMover = new Day5.CrateMover9000();
        example(crateMover);
        assertTrue("CMZ".contentEquals(crateMover.cratesOnTop()));
    }

    @Test
    public void testRearrange9001() throws IOException, URISyntaxException {
        var crateMover = new Day5.CrateMover9001();
        example(crateMover);
        assertTrue("MCD".contentEquals(crateMover.cratesOnTop()));
    }

    private void example(Day5.CrateMover<?> crateMover) throws IOException {
        var reader = new StringReader(sample);
        crateMover.load(reader);
        crateMover.move(1, '2', '1');
        crateMover.move(3, '1', '3');
        crateMover.move(2, '2', '1');
        crateMover.move(1, '1', '2');
    }

    @Test
    public void testLoadInputFile9000() throws IOException, URISyntaxException {
        var crateMover = new Day5.CrateMover9000();
        crateMover.loadFromInputFile();
        assertTrue("NGVQBRJQS".contentEquals(crateMover.cratesOnTop()));
    }

    @Test
    public void testLoadInputFile9001() throws IOException, URISyntaxException {
        var crateMover = new Day5.CrateMover9001();
        crateMover.loadFromInputFile();
        assertTrue("NGVQBRJQS".contentEquals(crateMover.cratesOnTop()));
    }

    @Test
    public void testReadInstructions9000() throws URISyntaxException, IOException {
        var crateMover = new Day5.CrateMover9000();
        try (var reader = crateMover.loadFromInputFile()) {
            crateMover.readInstructions(reader);
            assertTrue("GRTSWNJHH".contentEquals(crateMover.cratesOnTop()));
        }
    }

    @Test
    public void testReadInstructions9001() throws URISyntaxException, IOException {
        var crateMover = new Day5.CrateMover9001();
        try (var reader = crateMover.loadFromInputFile()) {
            crateMover.readInstructions(reader);
            assertTrue("QLFQDBBHM".contentEquals(crateMover.cratesOnTop()));
        }
    }
}

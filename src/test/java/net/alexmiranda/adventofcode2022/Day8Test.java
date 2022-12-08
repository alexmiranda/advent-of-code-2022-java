package net.alexmiranda.adventofcode2022;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

public class Day8Test {
    private static final String example = """
            30373
            25512
            65332
            33549
            35390
            """;

    @Test
    public void testExamplePart1() throws IOException {
        var reader = new StringReader(example);
        int[][] grid = Day8.read(reader);
        int result = Day8.countVisibleTrees(grid);
        assertEquals(21, result);
    }

    @Test
    public void testPuzzleInputPart1() throws URISyntaxException, IOException {
        int[][] grid = Day8.readInputFile();
        int result = Day8.countVisibleTrees(grid);
        assertEquals(1812, result);
    }

    @Test
    public void testScenicScore() throws IOException {
        var reader = new StringReader(example);
        int[][] grid = Day8.read(reader);
        int h = grid.length;
        int w = grid[0].length;
        assertEquals(4, Day8.scenicScore(grid, h, w, 1, 2));
        assertEquals(8, Day8.scenicScore(grid, h, w, 3, 2));
    }

    @Test
    public void testExamplePart2() throws IOException {
        var reader = new StringReader(example);
        int[][] grid = Day8.read(reader);
        assertEquals(8, Day8.highestScenicScore(grid));
    }

    @Test
    public void testPuzzleInputPart2() throws IOException, URISyntaxException {
        int[][] grid = Day8.readInputFile();
        assertEquals(315495, Day8.highestScenicScore(grid));
    }
}
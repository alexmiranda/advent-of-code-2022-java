package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class Day8 {
    private static final String INPUT = "2022/Day/8/input";

    public static int[][] readInputFile() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        try (var reader = Files.newBufferedReader(path)) {
            return read(reader);
        }
    }

    public static int[][] read(Reader reader) throws IOException {
        var list = new ArrayList<int[]>(100);
        try (var br = new BufferedReader(reader)) {
            String line = br.readLine();
            int cols = line.length();
            do {
                list.add(line.chars()
                        .map(c -> c - '0')
                        .toArray());
            } while ((line = br.readLine()) != null);
            var grid = new int[list.size()][cols];
            list.toArray(grid);
            return grid;
        }
    }

    public static int countVisibleTrees(int[][] grid) {
        int h = grid.length;
        int w = grid[0].length;
        int counter = 0;
        for (int x = 1; x < h - 1; x++) {
            for (int y = 1; y < w - 1; y++) {
                if (visible(grid, h, w, x, y)) {
                    counter++;
                }
            }
        }
        return h * 2 + (w - 2) * 2 + counter;
    }

    public static int highestScenicScore(int[][] grid) {
        int h = grid.length;
        int w = grid[0].length;
        int max = 0;
        for (int x = 1; x < h - 1; x++) {
            for (int y = 1; y < w - 1; y++) {
                int scenicScore = scenicScore(grid, h, w, x, y);
                if (scenicScore > max) {
                    max = scenicScore;
                }
            }
        }
        return max;
    }

    private static boolean visible(int[][] grid, int h, int w, int i, int j) {
        var n = grid[i][j];
        var left = IntStream.range(0, j).map(x -> grid[i][x]);
        var up = IntStream.range(0, i).map(x -> grid[x][j]);
        var right = IntStream.range(j + 1, w).map(x -> grid[i][x]);
        var down = IntStream.range(i + 1, h).map(x -> grid[x][j]);
        IntPredicate cond = x -> x < n;
        return left.allMatch(cond) ||
                up.allMatch(cond) ||
                right.allMatch(cond) ||
                down.allMatch(cond);
    }

    public static int scenicScore(int[][] grid, int h, int w, int i, int j) {
        var n = grid[i][j];
        var left = IntStream.iterate(j - 1, x -> x >= 0, x -> x - 1).map(x -> grid[i][x]);
        var up = IntStream.iterate(i - 1, x -> x >= 0, x -> x - 1).map(x -> grid[x][j]);
        var right = IntStream.range(j + 1, w).map(x -> grid[i][x]);
        var down = IntStream.range(i + 1, h).map(x -> grid[x][j]);
        var lc = countVisible(left, n);
        var uc = countVisible(up, n);
        var rc = countVisible(right, n);
        var dc = countVisible(down, n);
        return lc * uc * rc * dc;
    }

    private static int countVisible(IntStream trees, int height) {
        var iter = trees.iterator();
        int count = 0;
        while (iter.hasNext()) {
            var n = iter.next();
            count++;
            if (n >= height) {
                break;
            }
        }
        return count;
    }
}

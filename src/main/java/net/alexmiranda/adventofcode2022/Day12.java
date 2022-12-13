package net.alexmiranda.adventofcode2022;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Stream;

public class Day12 {
    private static final String INPUT = "2022/Day/12/input";

    // the first 4 bits are used to represent active edges
    // the last 7 bits will be the char itself
    private static final int flag = (4 * 4) << 7; // 2048
    private static final int vflag = 1 << 7; // 128

    private static final int lmask = 1 << 11;
    private static final int umask = lmask >> 1;
    private static final int rmask = umask >> 1;
    private static final int dmask = rmask >> 1;
    private static final int allmask = lmask + umask + rmask + dmask;

    static int[][] readInputFile() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        return toGraph(Files.lines(path));
    }

    static int[][] fromString(String s) {
        return toGraph(Stream.of(s.split("\n")));
    }

    private static int[][] toGraph(Stream<String> lines) {
        return lines.map(line -> line.chars().map(i -> i + allmask).toArray())
                .toArray(size -> new int[size][]);
    }

    static int findPosition(int[][] heightmap, int width, int length, char position) {
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < length; x++) {
                if (heightmap[y][x] % vflag == position) {
                    return y * length + x;
                }
            }
        }
        throw new RuntimeException("No start point found!");
    }

    static int shortestDistance(int[][] heightmap) {
        var width = heightmap.length;
        var length = heightmap[0].length;

        var end = findPosition(heightmap, width, length, 'E');
        var distances = new int[width][length];
        var queue = new LinkedList<Integer>();
        queue.add(end);

        while (!queue.isEmpty()) {
            var from = queue.poll();
            var fromRow = from / length;
            var fromCol = from % length;

            for (int to : edges(heightmap, width, length, from)) {
                queue.add(to);
                var toRow = to / length;
                var toCol = to % length;
                if (distances[toRow][toCol] == 0 || distances[fromRow][fromCol] + 1 < distances[toRow][toCol]) {
                    distances[toRow][toCol] = distances[fromRow][fromCol] + 1;
                }
            }
        }

        var start = findPosition(heightmap, width, length, 'S');
        var row = start / length;
        var col = start % length;
        assert distances[row][col] >= 26;
        return distances[row][col];
    }

    static char height(int value) {
        char c = (char) (value % vflag);
        return switch (c) {
            case 'S' -> 'a';
            case 'E' -> 'z';
            default -> c;
        };
    }

    static boolean isEdge(int from, int h, int v) {
        if (v % flag == 'S' || from % flag == 'E') {
            return false;
        }
        return height(from) >= h - 1;
    }

    static int[] edges(int[][] heightmap, int width, int length, int index) {
        var result = new ArrayList<Integer>(4);
        int row = index / length;
        int col = index % length;
        int v = heightmap[row][col];
        int h = height(v);

        // left
        if (col > 0 && (v & lmask) != 0) {
            var lval = heightmap[row][col - 1];
            if (isEdge(lval, h, v)) {
                heightmap[row][col] &= ~lmask;
                heightmap[row][col - 1] &= ~rmask;
                result.add(index - 1);
            }
        }

        // up
        if (row > 0 && (v & umask) != 0) {
            var uval = heightmap[row - 1][col];
            if (isEdge(uval, h, v)) {
                heightmap[row][col] &= ~umask;
                heightmap[row - 1][col] &= ~dmask;
                result.add(index - length);
            }
        }

        // right
        if (col < length - 1 && (v & rmask) != 0) {
            var rval = heightmap[row][col + 1];
            if (isEdge(rval, h, v)) {
                heightmap[row][col] &= ~rmask;
                heightmap[row][col + 1] &= ~lmask;
                result.add(index + 1);
            }
        }

        // down
        if (row < width - 1 && (v & dmask) != 0) {
            var dval = heightmap[row + 1][col];
            if (isEdge(dval, h, v)) {
                heightmap[row][col] &= ~dmask;
                heightmap[row + 1][col] &= ~umask;
                result.add(index + length);
            }
        }

        return result.stream().mapToInt(Integer::valueOf).toArray();
    }
}

package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day14 {
    private static final String INPUT = "/2022/Day/14/input";

    record Point(int x, int y) {
        static final Point SOURCE_OF_SAND = new Point(500, 0);

        static Point of(String s) {
            var parts = s.split(",");
            return new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }

    static class RegolithReservoir {
        private final HashMap<Integer, TreeSet<Integer>> scan = new HashMap<>();

        void addPath(Point... points) {
            for (int i = 1; i < points.length; i++) {
                var prev = points[i - 1];
                var curr = points[i];
                assert prev.x == curr.x || prev.y == curr.y;
                if (prev.x == curr.x) {
                    fillColumn(curr.x, prev.y, curr.y);
                } else if (prev.y == curr.y) {
                    range(prev.x, curr.x).forEach(pos -> addObject(pos, curr.y));
                }
            }
        }

        int simulate(Point sandSource) {
            int counter = 0;
            while (pour(sandSource)) {
                counter++;
            }
            return counter;
        }

        boolean pour(Point origin) {
            int x = origin.x;
            int y = origin.y + 1;
            var col = scan.get(x);
            if (col == null) {
                return false;
            }

            var rock = col.ceiling(y);
            if (rock == null || rock.equals(y)) {
                return false;
            }

            y = rock.intValue() - 1;
            var leftCol = scan.get(x - 1);
            if (leftCol == null) {
                return false;
            }

            if (!leftCol.contains(y + 1)) {
                return pour(new Point(x - 1, y));
            }

            var rightCol = scan.get(x + 1);
            if (rightCol == null) {
                return false;
            }

            if (!rightCol.contains(y + 1)) {
                return pour(new Point(x + 1, y));
            }

            addObject(x, y);
            return true;
        }

        void print(Writer w, int width, int length) throws IOException {
            var grid = new char[width][length];
            var offset = scan.keySet().stream().min((a, b) -> a - b).get();
            for (int j = 0; j <= length; j++) {
                var col = scan.get(j + offset);
                if (col == null) {
                    continue;
                }

                for (var i = 0; i < width; i++) {
                    grid[i][j] = '.';
                }
                for (var i : col) {
                    grid[i][j] = '#';
                }
            }
            for (int i = 0; i < width; i++) {
                w.write(grid[i]);
                w.append('\n');
            }
        }

        private void fillColumn(int col, int begin, int end) {
            scan.compute(col, (pos, set) -> {
                if (set == null) {
                    set = new TreeSet<>();
                }
                range(begin, end).forEach(set::add);
                return set;
            });
        }

        private void addObject(int x, int y) {
            scan.compute(x, (pos, set) -> {
                if (set == null) {
                    set = new TreeSet<>();
                }
                set.add(y);
                return set;
            });
        }

        private IntStream range(int from, int to) {
            if (from > to) {
                var tmp = from;
                from = to;
                to = tmp;
            }
            return IntStream.rangeClosed(from, to);
        }
    }

    static RegolithReservoir initialize(Reader reader) throws IOException {
        try (var br = new BufferedReader(reader)) {
            var reservoir = new RegolithReservoir();
            String line = null;
            while ((line = br.readLine()) != null) {
                reservoir.addPath(points(line));
            }
            return reservoir;
        }
    }

    static int countSettledSandCapacity(Reader reader) throws IOException {
        try (var br = new BufferedReader(reader)) {
            var reservoir = initialize(reader);
            return reservoir.simulate(Point.SOURCE_OF_SAND);
        }
    }

    static int countSettledSandCapacity() throws IOException {
        return countSettledSandCapacity(new InputStreamReader(Day14.class.getResourceAsStream(INPUT)));
    }

    private static Point[] points(String s) {
        return Stream.of(s.split(" -> "))
                .map(Point::of)
                .toArray(size -> new Point[size]);
    }
}

package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day14 {
    private static final String INPUT = "/2022/Day/14/input";
    static final String EXAMPLE = """
            498,4 -> 498,6 -> 496,6
            503,4 -> 502,4 -> 502,9 -> 494,9
            """;

    record Point(int x, int y) {
        static final Point SOURCE_OF_SAND = new Point(500, 0);

        static Point of(String s) {
            var parts = s.split(",");
            return new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }

    static class RegolithReservoir {
        protected final HashMap<Integer, TreeSet<Integer>> scan = new HashMap<>();
        protected int bottom = Integer.MIN_VALUE;
        private boolean simulationStarted = false;
        private final HashSet<Point> rocks = new HashSet<>();

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
                bottom = Math.max(bottom, Math.max(curr.y, prev.y) + 2);
            }
        }

        int simulate(Point sandSource) throws IOException {
            simulationStarted = true;
            int counter = 0;
            while (pour(sandSource)) {
                counter++;
                // print(new PrintWriter(System.out), 11, 21);
                // System.out.println("");
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
            for (int j = 0; j < length; j++) {
                for (var i = 0; i < width; i++) {
                    grid[i][j] = '.';
                }

                var col = scan.get(j + offset);
                if (col == null) {
                    continue;
                }
                for (var i : col) {
                    if (rocks.contains(new Point(j + offset, i))) {
                        grid[i][j] = '#';
                    } else {
                        grid[i][j] = 'o';
                    }
                }
            }
            grid[Point.SOURCE_OF_SAND.y][Point.SOURCE_OF_SAND.x - offset] = '+';
            for (int i = 0; i < width; i++) {
                w.write(grid[i]);
                System.out.print(grid[i]);
                w.append('\n');
                System.out.print('\n');
            }
            System.out.println("");
        }

        private void fillColumn(int col, int begin, int end) {
            scan.compute(col, (pos, set) -> {
                if (set == null) {
                    set = new TreeSet<>();
                }
                var iter = range(begin, end).iterator();
                while (iter.hasNext()) {
                    var i = iter.next();
                    if (!simulationStarted) {
                        rocks.add(new Point(col, i));
                    }
                    set.add(i);
                }
                return set;
            });
        }

        protected void addObject(int x, int y) {
            scan.compute(x, (pos, set) -> {
                if (set == null) {
                    set = new TreeSet<>();
                }
                if (!simulationStarted) {
                    rocks.add(new Point(x, y));
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

    static class RegolithReservoirRevised extends RegolithReservoir {
        @Override
        boolean pour(Point origin) {
            int x = origin.x;
            int y = origin.y + 1;
            var col = scan.get(x);
            assert y < bottom;
            if (col == null) {
                addObject(x, bottom - 1);
                return true;
            }

            var rock = col.ceiling(y);
            if (rock == null && y < bottom) {
                addObject(x, bottom - 1);
                return true;
            }

            if (rock.equals(y)) {
                if (y == Point.SOURCE_OF_SAND.y + 1) {
                    if (scan.get(x - 1).contains(y) && scan.get(x + 1).contains(y)) {
                        return false;
                    }
                }
            }

            y = rock.intValue() - 1;
            var leftCol = scan.get(x - 1);
            if (leftCol == null) {
                addObject(x - 1, bottom - 1);
                return true;
            }

            if (!leftCol.contains(y + 1)) {
                return pour(new Point(x - 1, y));
            }

            var rightCol = scan.get(x + 1);
            if (rightCol == null) {
                addObject(x + 1, bottom - 1);
                return true;
            }

            if (!rightCol.contains(y + 1)) {
                return pour(new Point(x + 1, y));
            }

            addObject(x, y);
            return true;
        }
    }

    static RegolithReservoir initialize(RegolithReservoir reservoir, Reader reader) throws IOException {
        try (var br = new BufferedReader(reader)) {
            String line = null;
            while ((line = br.readLine()) != null) {
                reservoir.addPath(points(line));
            }
            return reservoir;
        }
    }

    static int countSettledSandCapacity(Reader reader) throws IOException {
        try (var br = new BufferedReader(reader)) {
            var reservoir = initialize(new RegolithReservoir(), reader);
            return reservoir.simulate(Point.SOURCE_OF_SAND);
        }
    }

    static int countSettledSandCapacityRevised(Reader reader) throws IOException {
        try (var br = new BufferedReader(reader)) {
            var reservoir = initialize(new RegolithReservoirRevised(), reader);
            // the last one which's added here is the source of sand itself...
            return reservoir.simulate(Point.SOURCE_OF_SAND) + 1;
        }
    }

    static int countSettledSandCapacity() throws IOException {
        try (var reader = new InputStreamReader(Day14.class.getResourceAsStream(INPUT))) {
            return countSettledSandCapacity(reader);
        }
    }

    static int countSettledSandCapacityRevised() throws IOException {
        try (var reader = new InputStreamReader(Day14.class.getResourceAsStream(INPUT))) {
            return countSettledSandCapacityRevised(reader);
        }
    }

    private static Point[] points(String s) {
        return Stream.of(s.split(" -> "))
                .map(Point::of)
                .toArray(size -> new Point[size]);
    }

    public static void main(String[] args) {
        try (var reader = new StringReader(EXAMPLE)) {
            var reservoir = initialize(new RegolithReservoirRevised(), reader);
            reservoir.simulate(Point.SOURCE_OF_SAND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

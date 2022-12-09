package net.alexmiranda.adventofcode2022;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class Day9 {
    private static final String INPUT = "/2022/Day/9/input";

    record Point(int x, int y) implements Comparable<Point> {
        static final Point ZERO = new Point(0, 0);

        Point right() {
            return new Point(x + 1, y);
        }

        Point up() {
            return new Point(x, y + 1);
        }

        Point left() {
            return new Point(x - 1, y);
        }

        Point down() {
            return new Point(x, y - 1);
        }

        Point add(int dx, int dy) {
            return new Point(x + dx, y + dy);
        }

        @Override
        public int compareTo(Point other) {
            int d = this.y - other.y;
            if (d != 0) {
                return d;
            }
            return this.x < other.x ? -1 : this.x == other.x ? 0 : 1;
        }

        boolean neighbour(Point other) {
            if (other.equals(this)) {
                return true;
            }
            int dy = Math.abs(other.y - this.y);
            int dx = Math.abs(other.x - this.x);
            if (dy <= 1 && dx <= 1) {
                return true;
            }
            return false;
        }
    }

    static class Track {
        private final Point[] rope;
        private final Set<Point> visited;

        Track(int length) {
            this(Point.ZERO, length);
        }

        Track(Point start, int length) {
            assert length > 1 : "invalid number of knots: " + length;
            rope = new Point[length];
            for (int i = 0; i < length; i++) {
                rope[i] = start;
            }
            this.visited = new TreeSet<>();
            this.visited.add(start);
        }

        void move(String motion, int n) {
            assert n > 0 : "invalid motion: " + motion + " " + n;
            while (n > 0) {
                doMove(motion, 0);
                for (int i = 1; i < rope.length; i++) {
                    if (!followNext(i)) {
                        break;
                    }
                }
                visited.add(rope[rope.length - 1]);
                n--;
            }
        }

        private boolean followNext(int i) {
            var curr = rope[i];
            var next = rope[i - 1];
            if (curr.neighbour(next)) {
                return false;
            }

            var dx = curr.x == next.x ? 0 : (next.x - curr.x) / Math.abs(next.x - curr.x);
            var dy = curr.y == next.y ? 0 : (next.y - curr.y) / Math.abs(next.y - curr.y);
            var p = curr.add(dx, dy);
            rope[i] = p;
            return true;
        }

        private Point doMove(String motion, int i) {
            var curr = rope[i];
            rope[i] = switch (motion) {
                case "R" -> curr.right();
                case "U" -> curr.up();
                case "L" -> curr.left();
                case "D" -> curr.down();
                default -> throw new RuntimeException("invalid motion: " + motion);
            };
            return curr;
        }

        void read(Reader reader) {
            try (var scanner = new Scanner(reader)) {
                while (scanner.hasNext()) {
                    String motion = null;
                    switch (motion = scanner.next()) {
                        case "R":
                        case "U":
                        case "L":
                        case "D":
                            this.move(motion, scanner.nextInt());
                            break;
                    }
                }
            }
        }

        int countVisited() {
            return visited.size();
        }
    }

    public static int countVisited(Reader reader, int size) {
        var track = new Track(size);
        track.read(reader);
        return track.countVisited();
    }

    public static int countVisited(int size) {
        var reader = new InputStreamReader(Day9.class.getResourceAsStream(INPUT));
        return countVisited(reader, size);
    }
}

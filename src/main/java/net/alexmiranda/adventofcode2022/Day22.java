package net.alexmiranda.adventofcode2022;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.function.BiFunction;

public class Day22 {
    private static final String INPUT = "/2022/day/22/input";

    static class MonkeyMap {
        enum Direction {
            RIGHT(0, 1) {
                @Override
                Direction turn(char dir) {
                    return switch (dir) {
                        case 'L' -> UP;
                        case 'R' -> DOWN;
                        default -> invalidDirection(dir);
                    };
                }
            },
            DOWN(1, 1) {
                @Override
                Direction turn(char dir) {
                    return switch (dir) {
                        case 'L' -> RIGHT;
                        case 'R' -> LEFT;
                        default -> invalidDirection(dir);
                    };
                }
            },
            LEFT(2, -1) {
                @Override
                Direction turn(char dir) {
                    return switch (dir) {
                        case 'L' -> DOWN;
                        case 'R' -> UP;
                        default -> invalidDirection(dir);
                    };
                }
            },
            UP(3, -1) {
                @Override
                Direction turn(char dir) {
                    return switch (dir) {
                        case 'L' -> LEFT;
                        case 'R' -> RIGHT;
                        default -> invalidDirection(dir);
                    };
                }
            };

            private final int factor; // 1 for positive, -1 for negative
            private final int code;

            private Direction(int code, int factor) {
                this.code = code;
                this.factor = factor;
            }

            abstract Direction turn(char dir);

            protected Direction invalidDirection(char dir) {
                throw new IllegalStateException("Invalid direction: " + dir);
            }
        }

        static abstract class Path {
            final TreeSet<Integer> closures = new TreeSet<>();
            final int n;
            int start = -1;
            int end = -1;

            Path(int n) {
                this.n = n;
            }

            abstract int code();

            int length() {
                assert end > 0 && start >= 0 && end > start;
                return end - start + 1;
            }
        }

        static class Row extends Path {
            Row(int n) {
                super(n);
            }

            @Override
            int code() {
                return (n + 1) * 1000;
            }
        }

        static class Col extends Path {
            Col(int n) {
                super(n);
            }

            @Override
            int code() {
                return (n + 1) * 4;
            }
        }

        private final Map<String, Path> paths = new HashMap<>();
        private final String instructions;

        private Direction facing = Direction.RIGHT;
        private Path active = null;
        private Path passive = null;

        MonkeyMap(Readable input) {
            try (var scanner = new Scanner(input)) {
                scanner.useDelimiter("");

                int c = 0, r = 0, begin = 0, end = 0, prevEnd = 0;
                var row = paths.compute("R0", computeRow(0));
                char prev = '\0', curr = '\0';
                map_read_loop: while (scanner.hasNext()) {
                    prev = curr;
                    curr = scanner.next().charAt(0);

                    if (curr != '\n' && prev == '\n') {
                        row = paths.compute("R" + r, computeRow(r));
                    }

                    var col = paths.compute("C" + c, computeCol(c));
                    switch (curr) {
                        case ' ' -> {
                            if (row.start > -1 && row.end == -1) {
                                assert prev != ' ';
                                assert c == end;
                                row.end = c;
                            }
                            if (col.start > -1 && col.end == -1) {
                                col.end = r;
                            }
                        }
                        case '.', '#' -> {
                            end = c;
                            if (begin == 0) {
                                begin = c;
                            }

                            assert row.end == -1;
                            if (row.start == -1) {
                                row.start = c;
                            }

                            if (col.start == -1) {
                                assert col.end == -1;
                                col.start = r;
                            }

                            if (curr == '#') {
                                row.closures.add(c);
                                col.closures.add(r);
                            }
                        }
                        case '\n' -> {
                            assert prev != ' ';

                            // two consecutive new lines
                            if (prev == '\n') {
                                // terminate every unterminated col of the last row
                                for (int i = begin; i <= end; i++) {
                                    col = paths.get("C" + i);
                                    if (col.end == -1) {
                                        col.end = r - 1;
                                    }
                                }

                                // break out of the loop so we can start processing the instructions next
                                break map_read_loop;
                            }

                            // terminates every unterminated col of the last row
                            if (prevEnd > 0) {
                                assert r > 0;
                                for (int i = c; i <= prevEnd; i++) {
                                    col = paths.get("C" + i);
                                    if (col.end == -1) {
                                        col.end = r - 1;
                                    }
                                }
                            }

                            // terminates the current row
                            if (row.end == -1) {
                                assert end == c - 1;
                                row.end = end;
                                prevEnd = end;
                            }

                            r++;
                            c = 0;
                            begin = 0;
                            continue;
                        }
                    }
                    c++; // pun not intended
                }

                // got here from the end of the map
                assert scanner.hasNext();

                // read the rest of the input
                instructions = scanner.nextLine();
            }
        }

        int crackPassword() {
            active = paths.get("R0");
            passive = paths.get("C" + active.start);
            facing = Direction.RIGHT;

            try (var scanner = new Scanner(instructions)) {
                scanner.useDelimiter("");
                int n = 0;
                while (scanner.hasNext()) {
                    var c = scanner.next().charAt(0);
                    switch (c) {
                        case 'L', 'R' -> {
                            move(n);
                            n = 0;
                            switchDirection(c);
                        }
                        default -> {
                            assert Character.isDigit(c);
                            n = (n * 10) + c - '0';
                        }
                    }
                }
                move(n);
            }

            return active.code() + passive.code() + facing.code;
        }

        private void switchDirection(char dir) {
            facing = facing.turn(dir);
            // every turn causes row and column to be swaped
            var tmp = active;
            active = passive;
            passive = tmp;
        }

        private void move(int n) {
            int startPos = passive.n;
            int len = active.length();
            int endPos = startPos + (n * facing.factor);

            // are we blocked *before* the end of the path?
            if (facing.factor == 1) {
                var closure = active.closures.higher(startPos);
                if (closure != null && endPos >= closure) {
                    endTurn(closure - 1);
                    return;
                }
            } else if (facing.factor == -1) {
                var closure = active.closures.lower(startPos);
                if (closure != null && endPos <= closure) {
                    endTurn(closure + 1);
                    return;
                }
            }

            // are we wrapping around while moving *forward*?
            boolean clearPath = active.closures.isEmpty();
            if (endPos > active.end && facing.factor == 1) {
                int laps = (n - (active.end - startPos + 1)) / len;
                endPos = active.start + ((startPos - active.start) + n) % len;
                var closure = active.closures.ceiling(active.start);

                // if the first tile is blocked, then we remain at the last tile
                if (closure != null && closure.equals(active.start)) {
                    endTurn(active.end);
                    return;
                }

                // if 1. more than one *full* lap, or;
                // 2. a closure is met before the end position
                // then we must stop on the position *prior* to the closure
                if (!clearPath && endPos >= closure || laps > 0) {
                    endTurn(closure - 1);
                    return;
                }
            }

            // are we wrapping around while moving *backward*?
            if (endPos < active.start && facing.factor == -1) {
                int laps = (n - (startPos - active.start + 1)) / len;
                endPos = active.start + mod((startPos - active.start) - n, len);
                var closure = active.closures.floor(active.end);

                // if the last tile is blocked, then we remain at the first tile
                if (closure != null && closure.equals(active.end)) {
                    endTurn(active.start);
                    return;
                }

                // if 1. more than one *full* lap, or;
                // 2. a closure is met before the end position
                // then we must stop on the position *prior* to the closure
                if (!clearPath && endPos <= closure || laps > 0) {
                    endTurn(closure + 1);
                    return;
                }
            }

            // if none of the above, we move to the endPos
            endTurn(endPos);
        }

        private void endTurn(int endPos) {
            if (active instanceof Row) {
                passive = paths.get("C" + endPos);
            } else if (active instanceof Col) {
                passive = paths.get("R" + endPos);
            }
            assert passive != null;
        }

        private BiFunction<String, Path, Row> computeRow(int r) {
            return (String key, Path val) -> {
                return val == null ? new Row(r) : (Row) val;
            };
        }

        private BiFunction<String, Path, Col> computeCol(int c) {
            return (String key, Path val) -> {
                return val == null ? new Col(c) : (Col) val;
            };
        }

        private int mod(int dividend, int divisor) {
            var mod = dividend % divisor;
            if (mod < 0) {
                mod += divisor;
            }
            return mod;
        }
    }

    static Reader puzzleInput() {
        return new InputStreamReader(Day21.class.getResourceAsStream(INPUT));
    }
}

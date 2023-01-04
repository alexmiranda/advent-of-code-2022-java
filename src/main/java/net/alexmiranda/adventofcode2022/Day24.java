package net.alexmiranda.adventofcode2022;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Day24 {
    private static final String INPUT = "/2022/day/24/input";

    static class Valley {
        // @formatter:off
        private static final byte FREE      = 0b000_0000;
        private static final byte NORTHWARD = 0b000_0001;
        private static final byte EASTWARD  = 0b000_0010;
        private static final byte SOUTHWARD = 0b000_0100;
        private static final byte WESTWARD  = 0b000_1000;
        private static final byte WALL      = 0b001_1111;
        // @formatter:on

        private final byte[] data;
        private final int length;
        private final int width;
        private final int numberOfBlizzards;
        private final int startPos;
        private final int finishPos;
        private final int cycleSize;

        Valley(Readable input) {
            int length = 0, width = 0;
            var data = new ArrayList<Byte>();
            try (var scanner = new Scanner(input)) {
                scanner.useDelimiter("");
                var prev = '\0';
                while (scanner.hasNext()) {
                    var curr = scanner.next().charAt(0);
                    if (prev == '\n') {
                        data.ensureCapacity((length + 1) * width);
                    }
                    switch (curr) {
                        case '#' -> data.add(WALL);
                        case '.' -> data.add(FREE);
                        case '^' -> data.add(NORTHWARD);
                        case '>' -> data.add(EASTWARD);
                        case 'v' -> data.add(SOUTHWARD);
                        case '<' -> data.add(WESTWARD);
                        case '\n' -> {
                            length++;
                            width = data.size() / length;
                        }
                        default -> throw new IllegalArgumentException("input should not contain: " + curr);
                    }
                    prev = curr;
                }
            }

            this.length = length;
            this.width = width;
            this.data = new byte[data.size()];

            int numberOfBlizzards = 0;
            for (int i = 0; i < this.data.length; i++) {
                var b = data.get(i).byteValue();
                this.data[i] = b;
                if (b != FREE && b != WALL) {
                    numberOfBlizzards++;
                }
            }

            this.numberOfBlizzards = numberOfBlizzards;
            this.startPos = 1;
            this.finishPos = length * width - 2;
            this.cycleSize = lcm(length - 2, width - 2);
        }

        int shortestTime() {
            record Iteration(int pos, int minute, int distance) implements Comparable<Iteration> {
                @Override
                public int compareTo(Iteration other) {
                    var lhs = minute + distance;
                    var rhs = other.minute + other.distance;
                    return lhs - rhs;
                }
            }

            var queue = new PriorityQueue<Iteration>();
            var seen = new HashSet<Iteration>();
            var it = new Iteration(startPos, 0, manhattanDistance(startPos, finishPos));
            queue.add(it);
            seen.add(it);

            var precomputed = new HashMap<Integer, byte[]>();
            precomputed.put(0, this.data);
            while (!queue.isEmpty()) {
                it = queue.poll();

                // if it's the finish position, we basically arrived at the destination
                // with the shortest amount of steps, guaranteedly
                if (it.pos == finishPos) {
                    // set the final state of the valley at the last iteration
                    var data = getDataAt(precomputed, it.minute);
                    System.arraycopy(data, 0, this.data, 0, data.length);
                    return it.minute;
                }

                var minute = it.minute + 1;
                var data = getDataAt(precomputed, minute);

                // consider going to one of the neighbouring positions...
                var neighbours = neighbours(it.pos, data);
                for (var neighbour : neighbours) {
                    if (data[neighbour] == FREE) {
                        var next = new Iteration(neighbour, minute, manhattanDistance(neighbour, finishPos));
                        if (seen.add(next)) {
                            queue.add(next);
                        }
                    }
                }

                // consider remaining in the same position, if it's not occupied
                if (data[it.pos] == FREE) {
                    var next = new Iteration(it.pos, minute, it.distance);
                    if (seen.add(next)) {
                        queue.add(next);
                    }
                }
            }

            throw new RuntimeException("Impossible to reach the destination!");
        }

        private byte[] getDataAt(Map<Integer, byte[]> precomputed, int minute) {
            if (minute >= cycleSize) {
                return precomputed.get(minute % cycleSize);
            }
            return precomputed.computeIfAbsent(minute, (key) -> {
                var prev = precomputed.get(key - 1);
                return moveBlizzards(prev);
            });
        }

        void moveBlizzards() {
            var newData = moveBlizzards(this.data);
            System.arraycopy(newData, 0, this.data, 0, newData.length);
        }

        byte[] moveBlizzards(byte[] data) {
            // create a copy of the data to work with
            data = Arrays.copyOf(data, data.length);
            record Move(int pos, byte blizzard) {
            }

            // we know that in the first and last columns inside the external walls
            // where the start and finish positions are located, do NOT contain a
            // northward or southward blizzard, which means blizzard can't get in the
            // start or finish positions.
            // we start by collecting every blizzard we need to visit...
            int area = length * width;
            var queue = new ArrayDeque<Move>(numberOfBlizzards);
            for (int pos = width; pos < area - width - 1; pos++) {
                if (data[pos] != FREE && data[pos] != WALL) {
                    queue.add(new Move(pos, data[pos]));
                }
            }

            while (!queue.isEmpty()) {
                var move = queue.pop();
                assert move.blizzard != FREE && move.blizzard != WALL;

                byte pending = FREE;
                pending |= tryMove(move.pos, move.blizzard, NORTHWARD, data);
                pending |= tryMove(move.pos, move.blizzard, EASTWARD, data);
                pending |= tryMove(move.pos, move.blizzard, SOUTHWARD, data);
                pending |= tryMove(move.pos, move.blizzard, WESTWARD, data);

                // if we can't move all of the blizzards in this position at once, we need to
                // re-visit it later...
                if (pending != FREE) {
                    queue.add(new Move(move.pos, pending));
                }
            }

            return data;
        }

        byte tryMove(int pos, byte blizzard, byte direction, byte[] data) {
            // check if pos contains a blizzard facing the direction
            if ((blizzard & direction) != direction) {
                return 0;
            }

            var target = targetPosition(pos, direction, data);
            if ((data[target] & direction) == direction) {
                // there is a conflict with another blizzard in the target
                // position, thus we need to mark this blizzard to be
                // re-visited later
                return direction;
            }

            // move blizzard to target position
            data[target] |= direction;
            data[pos] ^= direction;
            return 0;
        }

        int manhattanDistance(int from, int dest) {
            var x1 = from % length;
            var y1 = from / length;
            var x2 = dest % length;
            var y2 = dest / length;
            return Math.abs(y2 - y1) + Math.abs(x2 - x1);
        }

        List<Integer> neighbours(int pos, byte[] data) {
            if (pos == startPos) {
                return Collections.singletonList(targetPosition(pos, SOUTHWARD, data));
            }

            if (pos == finishPos) {
                return List.of(targetPosition(pos, NORTHWARD, data));
            }

            var result = new ArrayList<Integer>(4);
            int row = pos / width;
            int col = pos % width;

            if (row >= 2) {
                var target = targetPosition(pos, NORTHWARD, data);
                assert data[target] != WALL;
                result.add(target);
            } else if (pos == startPos + width) {
                result.add(startPos);
            }

            if (col < width - 1) {
                var target = targetPosition(pos, EASTWARD, data);
                assert data[target] != WALL;
                result.add(target);
            }

            if (row < length - 1) {
                var target = targetPosition(pos, SOUTHWARD, data);
                assert data[target] != WALL;
                result.add(target);
            } else if (pos == finishPos - width) {
                result.add(finishPos);
            }

            if (col >= 2) {
                var target = targetPosition(pos, WESTWARD, data);
                assert data[target] != WALL;
                result.add(target);
            }

            return Collections.unmodifiableList(result);
        }

        int targetPosition(int pos, byte direction, byte[] data) {
            int area = length * width;
            return switch (direction) {
                case NORTHWARD -> {
                    var target = pos - width;
                    if (data[target] == WALL) {
                        target = area - width - (width - (pos % width));
                    }
                    yield target;
                }
                case EASTWARD -> {
                    var target = pos + 1;
                    if (data[target] == WALL) {
                        target = pos - width + 3;
                    }
                    yield target;
                }
                case SOUTHWARD -> {
                    var target = pos + width;
                    if (data[target] == WALL) {
                        target = width + (pos % width);
                    }
                    yield target;
                }
                case WESTWARD -> {
                    var target = pos - 1;
                    if (data[target] == WALL) {
                        target = pos + width - 3;
                    }
                    yield target;
                }
                default -> throw new IllegalStateException("invalid direction!");
            };
        }

        private int countBlizzards(byte blizzards) {
            int counter = 0;
            for (int i = 0; i < 4; i++) {
                counter += blizzards & 1;
                blizzards >>= 1;
            }
            return counter;
        }

        void print(Writer w) throws IOException {
            for (int i = 0; i < data.length; i++) {
                w.write(switch (data[i]) {
                    case WALL -> '#';
                    case FREE -> '.';
                    case NORTHWARD -> '^';
                    case EASTWARD -> '>';
                    case SOUTHWARD -> 'v';
                    case WESTWARD -> '<';
                    default -> '0' + countBlizzards(data[i]);
                });
                if ((i + 1) % width == 0) {
                    w.write('\n');
                }
            }
        }

        private static int lcm(int number1, int number2) {
            if (number1 == 0 || number2 == 0) {
                return 0;
            }
            int absNumber1 = Math.abs(number1);
            int absNumber2 = Math.abs(number2);
            int absHigherNumber = Math.max(absNumber1, absNumber2);
            int absLowerNumber = Math.min(absNumber1, absNumber2);
            int lcm = absHigherNumber;
            while (lcm % absLowerNumber != 0) {
                lcm += absHigherNumber;
            }
            return lcm;
        }
    }

    static Reader puzzleInput() {
        return new InputStreamReader(Day24.class.getResourceAsStream(INPUT));
    }
}

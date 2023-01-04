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
import java.util.PriorityQueue;
import java.util.Scanner;

public class Day24 {
    private static final String INPUT = "/2022/day/24/input";

    static class Valley {
        // @formatter:off
        private static final byte CLEAR     = 0b000_0000;
        private static final byte NORTHWARD = 0b000_0001;
        private static final byte EASTWARD  = 0b000_0010;
        private static final byte SOUTHWARD = 0b000_0100;
        private static final byte WESTWARD  = 0b000_1000;
        private static final byte WALL      = 0b001_1111;
        // @formatter:on

        private final HashMap<Integer, byte[]> precomputed;
        private final byte[] data;
        private final int length;
        private final int width;
        private final int numberOfBlizzards;
        private final int cycleSize;

        final int startPos;
        final int finishPos;

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
                        case '.' -> data.add(CLEAR);
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
                if (b != CLEAR && b != WALL) {
                    numberOfBlizzards++;
                }
            }

            this.numberOfBlizzards = numberOfBlizzards;
            this.startPos = 1;
            this.finishPos = length * width - 2;
            this.cycleSize = lcm(length - 2, width - 2);
            this.precomputed = new HashMap<>(cycleSize);
            this.precomputed.put(0, this.data);
        }

        int shortestTime() {
            return shortestTime(0, startPos, finishPos);
        }

        int shortestTimeRounds(int rounds) {
            var beginAt = startPos;
            var nextGoal = finishPos;
            var timeElapsed = 0;
            while (rounds > 0) {
                timeElapsed += shortestTime(timeElapsed, beginAt, nextGoal);
                var tmp = nextGoal;
                nextGoal = beginAt;
                beginAt = tmp;
                rounds--;
            }
            return timeElapsed;
        }

        int shortestTime(int timeElapsed, int beginAt, int goal) {
            record Iteration(int pos, int minute, int distance) implements Comparable<Iteration> {
                @Override
                public int compareTo(Iteration other) {
                    var delta = minute - other.minute;
                    if (delta == 0) {
                        delta = distance - other.distance;
                    }
                    return delta;
                }
            }

            var queue = new PriorityQueue<Iteration>();
            var seen = new HashSet<Iteration>();
            var it = new Iteration(beginAt, timeElapsed, manhattanDistance(beginAt, goal));
            queue.add(it);
            seen.add(it);

            while (!queue.isEmpty()) {
                it = queue.poll();

                // if it's the goal position, we basically arrived at the destination
                // with the shortest amount of steps, guaranteedly
                if (it.pos == goal) {
                    return it.minute - timeElapsed;
                }

                var minute = it.minute + 1;
                var data = getDataAt(minute);

                // consider going to one of the neighbouring positions...
                var neighbours = neighbours(it.pos, data);
                for (var neighbour : neighbours) {
                    if (data[neighbour] == CLEAR) {
                        var next = new Iteration(neighbour, minute, manhattanDistance(neighbour, goal));
                        if (seen.add(next)) {
                            queue.add(next);
                        }
                    }
                }

                // consider remaining in the same position, if it's not occupied
                if (data[it.pos] == CLEAR) {
                    var next = new Iteration(it.pos, minute, it.distance);
                    if (seen.add(next)) {
                        queue.add(next);
                    }
                }
            }

            throw new RuntimeException("Impossible to reach the destination!");
        }

        private byte[] getDataAt(int minute) {
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
            assert data != null && data.length == length * width;
            // create a copy of the data to work with
            data = Arrays.copyOf(data, data.length);

            record Move(int pos, byte blizzard) {
            }

            // we know that in the first and last columns inside the external walls
            // where the start and finish positions are located, do NOT contain a
            // start or finish positions.
            // we start by collecting every blizzard we need to visit...
            int area = length * width;
            var queue = new ArrayDeque<Move>(numberOfBlizzards);
            for (int pos = width; pos < area - width - 1; pos++) {
                if (data[pos] != CLEAR && data[pos] != WALL) {
                    queue.add(new Move(pos, data[pos]));
                }
            }

            while (!queue.isEmpty()) {
                var move = queue.pop();
                assert move.blizzard != CLEAR && move.blizzard != WALL;

                byte pending = CLEAR;
                pending |= tryMove(move.pos, move.blizzard, NORTHWARD, data);
                pending |= tryMove(move.pos, move.blizzard, EASTWARD, data);
                pending |= tryMove(move.pos, move.blizzard, SOUTHWARD, data);
                pending |= tryMove(move.pos, move.blizzard, WESTWARD, data);

                // if we can't move all of the blizzards in this position at once, we need to
                // re-visit it later...
                if (pending != CLEAR) {
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
                // the start position has only one single valid neighbour
                return Collections.singletonList(targetPosition(pos, SOUTHWARD, data));
            }

            if (pos == finishPos) {
                // equally the finish position has also only one single valid neighbour
                return List.of(targetPosition(pos, NORTHWARD, data));
            }

            var neighbours = new ArrayList<Integer>(4);
            int row = pos / width;
            int col = pos % width;

            // unlike blizzards, the expedition cannot "re-appear" on the opposite side of
            // the valley so we need to make sure that in order to be able to move NORTH, we
            // need to be in the SECOND row or after
            if (row >= 2) {
                var target = targetPosition(pos, NORTHWARD, data);
                assert data[target] != WALL && target == pos - width;
                neighbours.add(target);
            } else if (pos == startPos + width) {
                // exceptionally, if we are in the position directly south of the start
                // position, we should be able to move north back to the start position
                neighbours.add(startPos);
            }

            // to move EAST, we need to be in the SECOND TO LAST column or before
            if (col < width - 2) {
                var target = targetPosition(pos, EASTWARD, data);
                assert data[target] != WALL && target == pos + 1;
                neighbours.add(target);
            }

            // to move SOUTH, we need to be in the SECOND TO LAST row or before
            if (row < length - 2) {
                var target = targetPosition(pos, SOUTHWARD, data);
                assert data[target] != WALL && target == pos + width;
                neighbours.add(target);
            } else if (pos == finishPos - width) {
                // exceptionally, if we are in the position directly north of the finish
                // position, we should be able to move south back to the finish position
                neighbours.add(finishPos);
            }

            // to move WEST, we need to be in the SECOND column or after
            if (col >= 2) {
                var target = targetPosition(pos, WESTWARD, data);
                assert data[target] != WALL && target == pos - 1;
                neighbours.add(target);
            }

            return Collections.unmodifiableList(neighbours);
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

        private static int countBlizzards(byte blizzards) {
            int counter = 0;
            for (int i = 0; i < 4; i++) {
                counter += blizzards & 1;
                blizzards >>= 1;
            }
            return counter;
        }

        void print(Writer w) throws IOException {
            print(w, data, width);
        }

        static void print(Writer w, byte[] data, int width) throws IOException {
            for (int i = 0; i < data.length; i++) {
                w.write(switch (data[i]) {
                    case WALL -> '#';
                    case CLEAR -> '.';
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

        int length() {
            return this.length;
        }

        int width() {
            return this.width;
        }

        byte[] data() {
            return this.data;
        }
    }

    static Reader puzzleInput() {
        return new InputStreamReader(Day24.class.getResourceAsStream(INPUT));
    }
}

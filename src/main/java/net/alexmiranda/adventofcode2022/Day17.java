package net.alexmiranda.adventofcode2022;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;

public class Day17 {
    // @formatter:off
    private static final int LEFT  = 0b01000000_01000000_01000000_01000000;
    private static final int RIGHT = 0b00000001_00000001_00000001_00000001;

    static final int HORIZONTAL = 0b0_0000000_0_0000000_0_0000000_0_0011110;
    static final int CROSS      = 0b0_0000000_0_0001000_0_0011100_0_0001000;
    static final int LSHAPE     = 0b0_0000000_0_0000100_0_0000100_0_0011100;
    static final int VERTICAL   = 0b0_0010000_0_0010000_0_0010000_0_0010000;
    static final int SQUARE     = 0b0_0000000_0_0000000_0_0011000_0_0011000;
    // @formatter:on

    static final int[] ROCKS = new int[] {
            HORIZONTAL,
            CROSS,
            LSHAPE,
            VERTICAL,
            SQUARE,
    };

    static class Chamber {
        private final String jetPattern;
        private int nextMove = 0, nextRock = 0, space = 0;
        private byte[] rockpile = new byte[0];
        private long size = 0;

        Chamber(String jetPattern) {
            this.jetPattern = jetPattern;
        }

        long simulate(long n) {
            record CacheKey(int nextMove, int nextRock, int hash) {
            }

            record Cycle(long n, long size, int moves, byte[] rockPile) {
            }

            var cache = new HashMap<CacheKey, Cycle>();
            while (n > 0) {
                // have we seen this before? if so, we will advance forward quickly
                // by restoring the state from the cache...
                var cacheKey = new CacheKey(nextMove, nextRock, Arrays.hashCode(rockpile));
                var cycle = cache.get(cacheKey);
                if (cycle != null) {
                    long cycleSize = cycle.n - n;
                    assert (nextRock + cycleSize) % ROCKS.length == nextRock;

                    long delta = size - cycle.size + 1;
                    long repeat = n / cycleSize;
                    if (repeat > 0) {
                        size += delta * repeat;
                        rockpile = cycle.rockPile;
                        n %= repeat * cycleSize;

                        space = 0;
                        nextRock = (++nextRock) % ROCKS.length;
                        nextMove = (nextMove + cycle.moves) % jetPattern.length();
                        cache.clear();
                        continue;
                    }
                }

                var rock = ROCKS[nextRock];
                int moves = 0;
                // move 3 times freely in empty space
                for (int i = 0; i < 3; i++) {
                    rock = move(rock, 0);
                    nextMove = (++nextMove) % jetPattern.length();
                    moves++;
                }

                int depth = -1;
                while ((space & rock) == 0) {
                    // try to move, but take into account the settled rocks
                    rock = move(rock, space);
                    nextMove = (++nextMove) % jetPattern.length();
                    moves++;

                    // reached the bottom of the chamber
                    if (++depth == rockpile.length) {
                        break;
                    }

                    // fill out the space as we go deeper in the chamber contents
                    space = (space << 8) + rockpile[depth];
                }

                // grow the contents array as necessary
                int h = height(rock);
                var grow = h - depth;
                if (grow > 0) {
                    var newContents = new byte[rockpile.length + grow];
                    System.arraycopy(rockpile, 0, newContents, grow, rockpile.length);
                    rockpile = newContents;
                    depth += grow;
                    size += grow;
                }

                // rock's landed
                int highestFullLayer = -1;
                for (int i = 0; i < h; i++) {
                    var b = (byte) (rock >> (8 * i));
                    var full = (1 << 7) - 1;
                    int pos = depth - i - 1;
                    if (((rockpile[pos] |= b) & full) == full) {
                        highestFullLayer = pos;
                    }
                }

                // compact the whole thing up to the topmost full layer of rocks!
                if (highestFullLayer > 0) {
                    var newContents = new byte[highestFullLayer];
                    System.arraycopy(rockpile, 0, newContents, 0, highestFullLayer);
                    rockpile = newContents;

                    // store a copy of the rockpile so that it can be restored later, if a cycle is
                    // found
                    cache.put(cacheKey, new Cycle(n, size, moves, Arrays.copyOf(rockpile, rockpile.length)));
                }

                // round is done
                moves = 0;
                space = 0;
                nextRock = (++nextRock) % ROCKS.length;
                n--;
            }
            return size;
        }

        void print(Writer w) throws IOException {
            var shape = ROCKS[nextRock];
            printRock(w, shape);
            printEmptySpace(w);
            printPile(w, rockpile);
            printFloor(w);
        }

        int move(int shape, int space) {
            var dir = jetPattern.charAt(nextMove);
            return moveRock(shape, space, dir);
        }
    }

    static int moveRock(int rock, int space, char dir) {
        switch (dir) {
            case '<' -> {
                if (((LEFT | space) & rock) == 0) {
                    var res = rock << 1;
                    if ((space & res) == 0) {
                        return res;
                    }
                }
            }
            case '>' -> {
                if (((RIGHT | space) & rock) == 0) {
                    var res = rock >> 1;
                    if ((space & res) == 0) {
                        return res;
                    }
                }
            }
            default -> throw new RuntimeException("Direction " + dir + " is not supposed to exist!");
        }
        return rock;
    }

    static int height(int rock) {
        for (int i = 4; i >= 1; i--) {
            var boundary = 1 << (8 * (i - 1));
            if (rock >= boundary) {
                return i;
            }
        }
        return 0;
    }

    static void printRock(Writer w, int shape) throws IOException {
        var h = height(shape);
        var s = String.format("%" + (8 * h) + "s", Integer.toBinaryString(shape))
                .replace('1', '@')
                .replace('0', '.')
                .replace(' ', '.');

        for (int i = 0; i < h; i++) {
            var start = (8 * i) + 1;
            var end = start + 7;
            w.write('|');
            w.write(s.substring(start, end));
            w.write('|');
            w.write('\n');
        }
    }

    static void printPile(Writer w, byte[] contents) throws IOException {
        for (int i = 0; i < contents.length; i++) {
            var s = String.format("%7s", Integer.toBinaryString(contents[i]))
                    .replace('1', '#')
                    .replace('0', '.')
                    .replace(' ', '.');
            w.write('|');
            w.write(s);
            w.write('|');
            w.write('\n');
        }
    }

    private static void printEmptySpace(Writer w) throws IOException {
        for (int i = 0; i < 3; i++) {
            w.write("|.......|\n");
        }
    }

    private static void printFloor(Writer w) throws IOException {
        w.write("+-------+\n");
    }
}

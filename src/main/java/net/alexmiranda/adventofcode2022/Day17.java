package net.alexmiranda.adventofcode2022;

import java.io.IOException;
import java.io.Writer;

public class Day17 {
    private static final int LEFT = 0b01000000_01000000_01000000_01000000;
    private static final int RIGHT = 0b00000001_00000001_00000001_00000001;

    static final int HORIZONTAL_BAR = 0b0_0011110;
    static final int CROSS = 0b0_0001000_0_0011100_0_0001000;
    static final int L_REVERSED = 0b0_0000100_0_0000100_0_0011100;
    static final int VERTICAL_BAR = 0b0_0010000_0_0010000_0_0010000_0_0010000;
    static final int SQUARE = 0b0_0011000_0_0011000;

    static final int[] SHAPES = new int[] {
            HORIZONTAL_BAR,
            CROSS,
            L_REVERSED,
            VERTICAL_BAR,
            SQUARE,
    };

    static class Chamber {
        private final String jetPattern;
        private int nextMove = 0, nextShape = 0, space = 0;
        private byte[] contents = new byte[0];

        Chamber(String jetPattern) {
            this.jetPattern = jetPattern;
        }

        int simulate(int counter) {
            while (counter > 0) {
                var shape = SHAPES[nextShape];

                // move 3 times freely in empty space
                for (int i = 0; i < 3; i++) {
                    shape = move(shape, 0);
                    nextMove = (++nextMove) % jetPattern.length();
                }

                int depth = -1;
                while ((space & shape) == 0) {
                    // try to move, but take into account the settled rocks
                    shape = move(shape, space);
                    nextMove = (++nextMove) % jetPattern.length();
                    
                    // reached the bottom of the chamber
                    if (++depth == contents.length) {
                        break;
                    }

                    // fill out the space as we go deep into the chamber contents
                    space = (space << 8) + contents[depth];
                }

                // grow the contents array as necessary
                int h = height(shape);
                var grow = h - depth;
                if (grow > 0) {
                    var newContents = new byte[contents.length + grow];
                    System.arraycopy(contents, 0, newContents, grow, contents.length);
                    contents = newContents;
                    depth += grow;
                }

                // rock's landed
                for (int i = 0; i < h; i++) {
                    var b = (byte) (shape >> (8 * i));
                    contents[depth - i - 1] |= b;
                }

                space = 0;
                nextShape = (++nextShape) % SHAPES.length;
                counter--;
            }
            return contents.length;
        }

        void print(Writer w) throws IOException {
            var shape = SHAPES[nextShape];
            printShape(w, shape);
            printEmptySpace(w);
            printContents(w, contents);
            printFloor(w);
        }

        int move(int shape, int space) {
            var dir = jetPattern.charAt(nextMove);
            return moveShape(shape, space, dir);
        }
    }

    static int moveShape(int shape, int space, char dir) {
        switch (dir) {
            case '<' -> {
                if (((LEFT | space) & shape) == 0) {
                    var res = shape << 1;
                    if ((space & res) == 0) {
                        return res;
                    }
                }
            }
            case '>' -> {
                if (((RIGHT | space) & shape) == 0) {
                    var res = shape >> 1;
                    if ((space & res) == 0) {
                        return res;
                    }
                }
            }
            default -> throw new RuntimeException("Direction " + dir + " is not supposed to exist!");
        }
        return shape;
    }

    static int height(int shape) {
        for (int i = 4; i >= 1; i--) {
            var boundary = 1 << (8 * (i - 1));
            if (shape >= boundary) {
                return i;
            }
        }
        return 0;
    }

    static void printShape(Writer w, int shape) throws IOException {
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

    static void printContents(Writer w, byte[] contents) throws IOException {
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

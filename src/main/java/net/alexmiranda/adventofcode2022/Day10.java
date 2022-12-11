package net.alexmiranda.adventofcode2022;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Scanner;
import java.util.function.IntConsumer;

public class Day10 {

    static class ClockCircuit implements AutoCloseable {
        private Scanner scanner;
        private String instruction;
        private int cycle, counter, arg;
        private int x = 1;
        private IntConsumer listener;
        private char[][] screen = new char[6][40];

        ClockCircuit(Reader instructions) {
            scanner = new Scanner(instructions);
            listener = (i) -> {
            };
        }

        void ready() {
            if (!scanner.hasNext()) {
                return;
            }

            instruction = scanner.next();
            if (instruction.equals("addx")) {
                arg = scanner.nextInt();
            }

            tick();
        }

        private void tick() {
            draw();
            listener.accept(++cycle);
            switch (instruction) {
                case "addx": {
                    if (++counter == 2) {
                        x += arg;
                        counter = 0;
                        ready();
                        return;
                    }
                    break;
                }
                case "noop": {
                    ready();
                    return;
                }
            }
            tick();
        }

        private void draw() {
            var row = cycle / 40;
            var pos = cycle % 40;
            screen[row][pos] = Math.abs(x - pos) <= 1 ? '#' : '.';
        }

        void print(Writer w) throws IOException {
            for (int i = 0; i < screen.length; i++) {
                w.write(screen[i]);
                w.append('\n');
            }
        }

        int signalStrength() {
            return this.cycle * this.x;
        }

        void setListener(IntConsumer listener) {
            this.listener = listener;
        }

        @Override
        public void close() {
            scanner.close();
        }
    }
}

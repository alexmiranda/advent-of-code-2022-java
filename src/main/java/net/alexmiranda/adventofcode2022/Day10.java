package net.alexmiranda.adventofcode2022;

import java.io.Reader;
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

        void tick() {
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

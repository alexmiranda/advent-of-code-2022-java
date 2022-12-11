package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.IntUnaryOperator;
import java.util.stream.Stream;

public class Day11 {

    static class KeepAway {
        private final Monkey[] troop;

        KeepAway(Reader reader) throws IOException {
            var monkeys = new ArrayList<Monkey>(7);
            try (var br = new BufferedReader(reader)) {
                String line = null;
                Monkey monkey = null;
                while ((line = br.readLine()) != null) {
                    if (line.isEmpty()) {
                        monkeys.add(monkey);
                        continue;
                    }

                    line = line.stripLeading();
                    if (line.startsWith("Monkey")) {
                        monkey = new Monkey();
                    } else if (line.startsWith("Starting items:")) {
                        parseStartingItems(line, monkey);
                    } else if (line.startsWith("Operation:")) {
                        parseOperation(line, monkey);
                    } else if (line.startsWith("Test:")) {
                        parseTest(line, monkey);
                    } else if (line.startsWith("If true:")) {
                        parseCondition("true", line, monkey);
                    } else if (line.startsWith("If false:")) {
                        parseCondition("false", line, monkey);
                    }
                }
            }
            this.troop = monkeys.toArray(new Monkey[monkeys.size()]);
        }

        private void parseStartingItems(String line, Monkey monkey) {
            String[] parts = line.split(": ");
            var items = Stream.of(parts[1].split(", ")).mapToInt(Integer::valueOf).toArray();
            for (var item : items) {
                monkey.take(item);
            }
        }

        private void parseOperation(String line, Monkey monkey) {
            String[] parts = line.split(": ");
            var operation = new Operation(parts[1]);
            monkey.setOperation(operation);
        }

        private void parseTest(String line, Monkey monkey) {
            int lastSpaceIndex = line.lastIndexOf(' ');
            monkey.setTestDivisor(Integer.parseInt(line.substring(lastSpaceIndex + 1)));
        }

        private void parseCondition(String condition, String line, Monkey monkey) {
            int lastSpaceIndex = line.lastIndexOf(' ');
            var v = Integer.parseInt(line.substring(lastSpaceIndex + 1));
            if (condition.equals("true")) {
                monkey.setWhenTrue(v);
            } else if (condition.equals("false")) {
                monkey.setWhenFalse(v);
            }
        }

        void play(int n) {
            while (n > 0) {
                for (var monkey : troop) {
                    monkey.inspect(troop);
                }
                n--;
            }
        }

        int monkeyBusiness() {
            int top1 = 0, top2 = 0;
            for (var monkey : troop) {
                if (monkey.inspectedItems >= top1) {
                    top2 = top1;
                    top1 = monkey.inspectedItems;
                } else if (monkey.inspectedItems > top2) {
                    top2 = monkey.inspectedItems;
                }
            }
            return top1 * top2;
        }
    }

    static class Monkey {
        private final Queue<Integer> items = new ArrayDeque<>();
        private int inspectedItems = 0;
        private int testDivisor;
        private IntUnaryOperator operation;
        private int whenTrue;
        private int whenFalse;

        void take(int item) {
            items.add(item);
        }

        void inspect(Monkey[] troop) {
            while (!items.isEmpty()) {
                this.inspectedItems++;
                var worryLevel = items.poll();
                worryLevel = operation.applyAsInt(worryLevel) / 3;
                if (worryLevel % testDivisor == 0) {
                    troop[whenTrue].take(worryLevel);
                } else {
                    troop[whenFalse].take(worryLevel);
                }
            }
        }

        void setOperation(IntUnaryOperator operation) {
            this.operation = operation;
        }

        void setTestDivisor(int testDivisor) {
            this.testDivisor = testDivisor;
        }

        void setWhenTrue(int whenTrue) {
            this.whenTrue = whenTrue;
        }

        void setWhenFalse(int whenFalse) {
            this.whenFalse = whenFalse;
        }
    }

    static class Operation implements IntUnaryOperator {
        private final String expr;

        Operation(String expr) {
            this.expr = expr.substring(6); // new = blah blah
        }

        @Override
        public int applyAsInt(int old) {
            var operands = new Stack<Integer>();
            String operator = null;
            try (var scanner = new Scanner(expr)) {
                while (scanner.hasNext()) {
                    String token = scanner.next();
                    switch (token) {
                        case "+", "*" -> operator = token;
                        case "old" -> operands.push(old);
                        default -> operands.push(Integer.valueOf(token));
                    }
                }
            }

            assert operands.size() == 2 : "invalid expression: " + expr;
            assert operator != null : "invalid operator in expr: " + expr;

            var a = operands.pop();
            var b = operands.pop();
            return switch (operator) {
                case "+" -> a + b;
                case "*" -> a * b;
                default -> throw new RuntimeException("This should never happen...");
            };
        }
    }

}

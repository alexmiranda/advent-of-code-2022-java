package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.HashMap;

public class Day21 {
    private static final String INPUT = "/2022/day/21/input";

    interface Job {
        BigDecimal shout(Context ctx);

        static Job fromString(String s) {
            var parts = s.split(" ");
            if (parts.length == 1) {
                return new NumberJob(Integer.parseInt(parts[0]));
            } else if (parts.length == 3) {
                return new MathJob(parts[0], parts[2], parts[1].charAt(0));
            }
            throw new IllegalArgumentException("Invalid line: " + s);
        }
    }

    static final class NumberJob implements Job {
        private final BigDecimal number;

        NumberJob(int number) {
            this.number = BigDecimal.valueOf(number);
        }

        @Override
        public BigDecimal shout(Context ctx) {
            return number;
        }
    }

    static final class MathJob implements Job {
        private final String left;
        private final String right;
        private final char operator;

        private BigDecimal answer = null;

        MathJob(String left, String right, char operator) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        @Override
        public BigDecimal shout(Context ctx) {
            if (answer == null) {
                var lhs = ctx.monkeyJobs.get(left).shout(ctx);
                var rhs = ctx.monkeyJobs.get(right).shout(ctx);
                answer = solve(lhs, rhs);
            }
            return answer;
        }

        private BigDecimal solve(BigDecimal a, BigDecimal b) {
            return switch (operator) {
                case '+' -> a.add(b);
                case '-' -> a.subtract(b);
                case '*' -> a.multiply(b);
                case '/' -> {
                    // assert a % b == 0;
                    yield a.divide(b);
                }
                default -> throw new IllegalStateException("invalid operator: " + operator);
            };
        }
    }

    static class Context {
        final HashMap<String, Job> monkeyJobs = new HashMap<>(15);

        Context(Reader reader) throws IOException {
            try (var br = new BufferedReader(reader)) {
                br.lines().forEach(line -> {
                    var parts = line.split(": ");
                    monkeyJobs.put(parts[0], Job.fromString(parts[1]));
                });
            }
        }

        String solve(String monkey) {
            return monkeyJobs.get(monkey).shout(this).toString();
        }
    }

    static Reader puzzleInput() {
        return new InputStreamReader(Day21.class.getResourceAsStream(INPUT));
    }

}

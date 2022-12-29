package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;

public class Day21 {
    private static final String INPUT = "/2022/day/21/input";

    interface Job {
        BigDecimal shout(Context ctx, boolean withCache);

        default BigDecimal shout(Context ctx) {
            return this.shout(ctx, true);
        }

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
        public BigDecimal shout(Context ctx, boolean withCache) {
            return number;
        }
    }

    static final class MathJob implements Job {
        private final String left, right;
        private final char operator;
        private BigDecimal answer = null;

        MathJob(String left, String right, char operator) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        @Override
        public BigDecimal shout(Context ctx, boolean withCache) {
            if (answer == null || !withCache) {
                answer = solve(ctx, withCache);
            }
            return answer;
        }

        private BigDecimal solve(Context ctx, boolean withCache) {
            var lhs = ctx.monkeyJobs.get(left).shout(ctx, withCache);
            var rhs = ctx.monkeyJobs.get(right).shout(ctx, withCache);
            return switch (operator) {
                case '+' -> lhs.add(rhs);
                case '-' -> lhs.subtract(rhs);
                case '*' -> lhs.multiply(rhs);
                case '/' -> lhs.divide(rhs);
                default -> invalidOperator();
            };
        }

        // invert is basically solving the tiniest of equations by computing the reverse
        // math operation over the constant side and leaving out the variable...
        BigDecimal invert(Context ctx, boolean leftSide, BigDecimal target) {
            var constant = computeOperand(ctx, !leftSide);
            return switch (operator) {
                case '+' -> target.subtract(constant);
                case '-' -> target.add(constant);
                case '*' -> target.divide(constant);
                case '/' -> target.multiply(constant);
                default -> invalidOperator();
            };
        }

        private BigDecimal invalidOperator() {
            throw new IllegalStateException("Invalid operator: " + operator);
        }

        BigDecimal computeOperand(Context ctx, boolean leftSide) {
            return (leftSide ? ctx.monkeyJobs.get(left) : ctx.monkeyJobs.get(right)).shout(ctx);
        }
    }

    static final class StupidHuman implements Job {
        long i = 0;
        boolean detectedBranch = false;

        @Override
        public BigDecimal shout(Context ctx, boolean withCache) {
            if (!detectedBranch) {
                // this will cause a NumberFormatException
                return BigDecimal.valueOf(Double.NaN);
            }
            return BigDecimal.valueOf(i++);
        }
    }

    static final class HumanJob implements Job {
        @Override
        public BigDecimal shout(Context ctx, boolean withCache) throws NumberFormatException {
            record Trail(MathJob job, boolean leftSide) {
            }

            var trail = new Stack<Trail>();
            var prev = "humn";
            var monkey = ctx.monkeyJobs.entrySet().stream()
                    .filter(predicate(prev))
                    .map(Map.Entry::getKey)
                    .findFirst().get();

            // read a trail of jobs that depend on the human shouting the right number
            // up to the root node
            while (true) {
                var job = (MathJob) ctx.monkeyJobs.get(monkey);
                assert job != null;

                var leftSide = job.left.equals(prev);
                trail.push(new Trail(job, leftSide));
                if (monkey.equals("root")) {
                    break;
                }

                prev = monkey;
                monkey = ctx.monkeyJobs.entrySet().stream()
                        .filter(predicate(prev))
                        .map(Map.Entry::getKey)
                        .findFirst().get();
            }

            // on top of the stack is the root element
            var step = trail.pop();

            // first we need to compute the constant side of the root node
            BigDecimal target = step.job.computeOperand(ctx, !step.leftSide);

            // now one step at a time we compute the inverse of the operation on each node
            while (!trail.isEmpty()) {
                step = trail.pop();
                target = step.job.invert(ctx, step.leftSide, target);
            }
            return target;
        }

        private Predicate<Map.Entry<String, Job>> predicate(String monkey) {
            return (entry) -> {
                var job = entry.getValue();
                if (job instanceof MathJob math) {
                    return math.left.equals(monkey) || math.right.equals(monkey);
                }
                return false;
            };
        }
    }

    static class Context {
        final HashMap<String, Job> monkeyJobs = new HashMap<>(15);

        Context(Reader reader, boolean human) throws IOException {
            this(reader, human, false); // uses stupid human by default
        }

        Context(Reader reader, boolean human, boolean clever) throws IOException {
            try (var br = new BufferedReader(reader)) {
                br.lines().forEach(line -> {
                    var parts = line.split(": ");
                    var monkey = parts[0];
                    if (human && monkey.equals("humn")) {
                        var humn = clever ? new HumanJob() : new StupidHuman();
                        monkeyJobs.put(monkey, humn);
                        return;
                    }
                    monkeyJobs.put(monkey, Job.fromString(parts[1]));
                });
            }
        }

        String solve(String monkey) {
            return monkeyJobs.get(monkey).shout(this).toString();
        }

        String solvePart2(String monkey) {
            var root = (MathJob) monkeyJobs.get("root");
            var humn = (StupidHuman) monkeyJobs.get(monkey);

            BigDecimal constant = null;
            Job variable = null;
            try {
                constant = monkeyJobs.get(root.left).shout(this);
                variable = monkeyJobs.get(root.right);
            } catch (NumberFormatException e) {
                constant = monkeyJobs.get(root.right).shout(this);
                variable = monkeyJobs.get(root.left);
            }

            humn.detectedBranch = true;
            while (true) {
                try {
                    var result = variable.shout(this, false);
                    if (result.equals(constant)) {
                        return Long.toString(humn.i - 1);
                    }
                } catch (Exception e) {
                    // we gotta be persistent!
                }
            }
        }

        String solvePart2CleverWay(String monkey) {
            var humn = (HumanJob) monkeyJobs.get(monkey);
            var result = humn.shout(this);
            return Long.toString(result.longValue());
        }
    }

    static Reader puzzleInput() {
        return new InputStreamReader(Day21.class.getResourceAsStream(INPUT));
    }
}

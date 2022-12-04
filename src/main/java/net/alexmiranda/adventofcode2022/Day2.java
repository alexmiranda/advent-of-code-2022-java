package net.alexmiranda.adventofcode2022;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Day2 {
    private static final String INPUT = "2022/Day/2/input";

    enum Shape {
        ROCK(1) {
            @Override
            protected int outcome(Shape against) {
                return switch (against) {
                    case ROCK -> 3;
                    case PAPER -> 0;
                    case SCISSOR -> 6;
                };
            }
        },
        PAPER(2) {
            @Override
            protected int outcome(Shape against) {
                return switch (against) {
                    case ROCK -> 6;
                    case PAPER -> 3;
                    case SCISSOR -> 0;
                };
            }
        },
        SCISSOR(3) {
            @Override
            protected int outcome(Shape against) {
                return switch (against) {
                    case ROCK -> 0;
                    case PAPER -> 6;
                    case SCISSOR -> 3;
                };
            }
        };

        private final int baseScore;

        private Shape(int baseScore) {
            this.baseScore = baseScore;
        }

        protected abstract int outcome(Shape answer);

        int calculateScore(Shape against) {
            return this.baseScore + this.outcome(against);
        }

        static Shape fromString(String s) {
            return switch (s) {
                case "A", "X" -> Shape.ROCK;
                case "B", "Y" -> Shape.PAPER;
                case "C", "Z" -> Shape.SCISSOR;
                default -> throw new RuntimeException("invalid input");
            };
        }

        Shape fromOutcomeString(String s) {
            return switch (s) {
                case "Y" -> this;
                case "X", "Z" -> {
                    yield Stream.of(Shape.values())
                            .filter(shape -> shape != this)
                            .filter(shape -> {
                                int outcome = shape.outcome(this);
                                if (s.equals("X") && outcome == 0) {
                                    return true;
                                } else if (s.equals("Z") && outcome == 6) {
                                    return true;
                                }
                                return false;
                            }).findFirst()
                            .get();
                }
                default -> throw new RuntimeException("invalid input");
            };
        }
    }

    public static int totalScore() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        try (var lines = Files.lines(path)) {
            return lines.mapToInt(s -> {
                var opponent = Shape.fromString(s.substring(0, 1));
                var answer = Shape.fromString(s.substring(2, 3));
                return answer.calculateScore(opponent);
            }).sum();
        }
    }

    public static int totalScoreOutcomes() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        try (var lines = Files.lines(path)) {
            return lines.mapToInt(s -> {
                var opponent = Shape.fromString(s.substring(0, 1));
                var answer = opponent.fromOutcomeString(s.substring(2, 3));
                return answer.calculateScore(opponent);
            }).sum();
        }
    }
}

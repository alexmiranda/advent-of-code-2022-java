package net.alexmiranda.adventofcode2022;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Day3 {
    private static final String INPUT = "2022/Day/3/input";

    public static int sumOfPriorities() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        try (var lines = Files.lines(path)) {
            return lines.mapToInt(Day3::commonItemsPriority).sum();
        }
    }

    public static int sumOfGroupPriorities() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        try (var lines = Files.lines(path)) {
            var counter = new AtomicInteger(-1);
            var map = lines.collect(
                    Collectors.groupingBy(g -> counter.incrementAndGet() / 3,
                            Collectors.collectingAndThen(Collectors.toList(), l -> l.toArray(new String[3]))));

            return map
                    .values()
                    .stream()
                    .mapToInt(Day3::commonGroupPriority)
                    .sum();
        }
    }

    static int commonItemsPriority(String line) {
        assert line.length() % 2 == 0 : "odd number of characters: " + line;
        int middle = line.length() / 2;
        var lhs = line.subSequence(0, middle);
        var rhs = line.subSequence(middle, line.length());

        assert lhs.length() == rhs.length()
                : "left and right compartments should have the same amount of items: " + line;

        int sum = lhs.chars()
                .distinct()
                .filter(c1 -> rhs.chars().anyMatch(c2 -> c2 == c1))
                .map(Day3::toPriority)
                .sum();

        assert sum > 0 : "no common items found in the two compartments: " + line;
        return sum;
    }

    static int commonGroupPriority(String[] lines) {
        assert lines.length == 3 : "invalid group size: " + lines.length;
        assert lines[0] != null : "first line should not be empty";
        assert lines[1] != null : "second line should not be empty";
        assert lines[2] != null : "third line should not be empty";

        return lines[0].chars().distinct()
                .filter(c -> lines[1].indexOf(c) >= 0 && lines[2].indexOf(c) >= 0)
                .map(Day3::toPriority)
                .sum();
    }

    private static int toPriority(int i) {
        if (Character.isLowerCase(i)) {
            return i - 'a' + 1;
        }
        return 26 + toPriority(Character.toLowerCase(i));
    }
}

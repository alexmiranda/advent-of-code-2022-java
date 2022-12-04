package net.alexmiranda.adventofcode2022;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Day4 {
    private static final String INPUT = "2022/Day/4/input";

    record AssignmentPair(int a1, int a2, int b1, int b2) {
    }

    public static long countAssignmentsFullyContained() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        try (var lines = Files.lines(path)) {
            return lines.filter(Day4::fullyContained).count();
        }
    }

    public static long countAssignmentsOverlapping() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        try (var lines = Files.lines(path)) {
            return lines.filter(Day4::overlap).count();
        }
    }

    static boolean fullyContained(String line) {
        var p = parseAssignmentPair(line);
        return (p.a1 >= p.b1 && p.a2 <= p.b2) || (p.b1 >= p.a1 && p.b2 <= p.a2);
    }

    static boolean overlap(String line) {
        var p = parseAssignmentPair(line);
        return (p.a1 >= p.b1 && p.a1 <= p.b2) ||
                (p.a2 >= p.b1 && p.a2 <= p.b2) ||
                (p.b1 >= p.a1 && p.b1 <= p.a1) ||
                (p.b2 >= p.a1 && p.b2 <= p.a2);
    }

    private static AssignmentPair parseAssignmentPair(String line) {
        int firstSep = line.indexOf("-");
        int a1 = Integer.parseInt(line.substring(0, firstSep));
        int comma = line.indexOf(",");
        int a2 = Integer.parseInt(line.substring(firstSep + 1, comma));
        int lastSep = line.lastIndexOf("-");
        int b1 = Integer.parseInt(line.substring(comma + 1, lastSep));
        int b2 = Integer.parseInt(line.substring(lastSep + 1, line.length()));
        return new AssignmentPair(a1, a2, b1, b2);
    }
}

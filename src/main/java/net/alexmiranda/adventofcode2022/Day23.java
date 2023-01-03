package net.alexmiranda.adventofcode2022;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day23 {
    private static final String INPUT = "/2022/day/23/input";

    private static final Rule[] RULES = new Rule[] {
            new Rule(Direction.N, Direction.NW, Direction.N, Direction.NE),
            new Rule(Direction.S, Direction.SW, Direction.S, Direction.SE),
            new Rule(Direction.W, Direction.NW, Direction.W, Direction.SW),
            new Rule(Direction.E, Direction.NE, Direction.E, Direction.SE),
    };

    record Location(int x, int y) {
    }

    enum Direction {
        NW(-1, -1),
        N(0, -1),
        NE(1, -1),
        W(-1, 0),
        E(1, 0),
        SW(-1, 1),
        S(0, 1),
        SE(1, 1);

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    record Rule(Direction direction, Direction... checks) {
    }

    static class Elf {
        private Location loc;

        Elf(int x, int y) {
            this.loc = new Location(x, y);
        }

        Optional<Location> propose(Collection<Elf> elves, int firstChoice) {
            if (noNeighbours(elves)) {
                return Optional.empty();
            }

            for (int i = 0; i < RULES.length; i++) {
                var rule = RULES[(firstChoice + i) % RULES.length];
                var locs = Arrays.stream(rule.checks()).map(l -> new Location(loc.x + l.dx, loc.y + l.dy));
                if (locs.allMatch(l -> noElfPresent(elves, l))) {
                    return Optional.of(new Location(loc.x + rule.direction.dx, loc.y + rule.direction.dy));
                }
            }
            return Optional.empty();
        }

        void move(Location loc) {
            this.loc = loc;
        }

        private boolean noNeighbours(Collection<Elf> elves) {
            return Arrays.stream(Direction.values())
                    .map(l -> new Location(loc.x + l.dx, loc.y + l.dy))
                    .allMatch(l -> noElfPresent(elves, l));
        }

        private boolean noElfPresent(Collection<Elf> elves, Location loc) {
            return !elves.stream().anyMatch(elf -> elf.loc.x == loc.x() && elf.loc.y == loc.y());
        }
    }

    static class Grove {
        private final List<Elf> elves = new ArrayList<>();
        private int nextFirstRule = 0;

        record Result(boolean moved, int emptyTilesCount) {
        }

        Grove(Readable input) {
            try (var scanner = new Scanner(input)) {
                scanner.useDelimiter("");
                int x = 0, y = 0;
                while (scanner.hasNext()) {
                    var c = scanner.next().charAt(0);
                    switch (c) {
                        case '#' -> elves.add(new Elf(x, y));
                        case '\n' -> {
                            x = 0;
                            y++;
                            continue;
                        }
                    }
                    x++;
                }
            }
        }

        int countEmptyTiles(int n) {
            int result = 0;
            for (int i = 0; i < n; i++) {
                result = simulate(nextFirstRule).emptyTilesCount;
                nextFirstRule = (nextFirstRule + 1) % RULES.length;
            }
            return result;
        }

        int roundsUntilNoElfMoves() {
            int counter = 1;
            var result = simulate(nextFirstRule);
            while (result.moved) {
                nextFirstRule = (nextFirstRule + 1) % RULES.length;
                result = simulate(nextFirstRule);
                counter++;
            }
            return counter;
        }

        Result simulate(int firstRule) {
            var proposals = new HashMap<Location, List<Elf>>(elves.size(), .9f);
            for (var elf : elves) {
                elf.propose(elves, firstRule).ifPresent(loc -> {
                    proposals.compute(loc, (key, value) -> {
                        if (value == null) {
                            return new ArrayList<>(List.of(elf));
                        }
                        value.add(elf);
                        return value;
                    });
                });
                ;
            }

            var moved = false;
            for (var entry : proposals.entrySet()) {
                if (entry.getValue().size() > 1) {
                    continue;
                }
                var elf = entry.getValue().get(0);
                var loc = entry.getKey();
                elf.move(loc);
                moved = true;
            }

            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
            for (var elf : elves) {
                minX = Math.min(minX, elf.loc.x);
                minY = Math.min(minY, elf.loc.y);
                maxX = Math.max(maxX, elf.loc.x);
                maxY = Math.max(maxY, elf.loc.y);
            }

            var w = maxX - minX + 1;
            var h = maxY - minY + 1;
            return new Result(moved, w * h - elves.size());
        }

        void print(Writer w) throws IOException {
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
            for (var elf : elves) {
                minX = Math.min(minX, elf.loc.x);
                minY = Math.min(minY, elf.loc.y);
                maxX = Math.max(maxX, elf.loc.x);
                maxY = Math.max(maxY, elf.loc.y);
            }

            var width = maxX - minX + 1;
            var elvesPerRow = elves.stream().collect(Collectors.groupingBy(elf -> elf.loc.y(),
                    Collectors.mapping(elf -> elf.loc.x(), Collectors.toSet())));

            var sb = new StringBuilder();
            for (int i = minY; i <= maxY; i++) {
                sb.append(".".repeat(width));
                for (var j : elvesPerRow.getOrDefault(i, Collections.emptySet())) {
                    sb.setCharAt(sb.length() - (width - (j - minY)), '#');
                }
                sb.append('\n');
            }

            w.write(sb.toString());
        }
    }

    static Reader puzzleInput() {
        return new InputStreamReader(Day23.class.getResourceAsStream(INPUT));
    }
}

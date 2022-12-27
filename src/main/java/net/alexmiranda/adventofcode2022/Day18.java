package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day18 {
    private static final String INPUT = "/2022/day/18/input";

    record Coord(int x, int y, int z) {
        record Perimeter(Coord min, Coord max) {
        }

        Stream<Coord> adjacents() {
            return IntStream.of(-1, 1)
                    .boxed()
                    .flatMap(i -> Stream.of(new Coord(x + i, y, z), new Coord(x, y + i, z), new Coord(x, y, z + i)));
        }

        private boolean within(Perimeter perimeter) {
            var min = perimeter.min;
            var max = perimeter.max;
            return x >= min.x && x <= max.x &&
                    y >= min.y && y <= max.y &&
                    z >= min.z && z <= max.z;
        }

        private static Perimeter determinePerimeter(Set<Coord> coords) {
            int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
            for (var coord : coords) {
                min = Math.min(min, coord.x);
                min = Math.min(min, coord.y);
                min = Math.min(min, coord.z);
                max = Math.max(max, coord.x);
                max = Math.max(max, coord.y);
                max = Math.max(max, coord.z);
            }
            return new Perimeter(new Coord(min - 1, min - 1, min - 1), new Coord(max + 1, max + 1, max + 1));
        }

        static Set<Coord> reachableByWater(Set<Coord> coords) {
            var result = new HashSet<Coord>();
            var perimeter = determinePerimeter(coords);

            // origin is just outside of the perimeter, but sufficiently close to find its
            // way into the perimeter
            var origin = new Coord(perimeter.min.x - 1, perimeter.min.y, perimeter.min.z);
            assert !coords.contains(origin);
            assert !origin.within(perimeter);
            assert origin.adjacents().anyMatch(adjacent -> !coords.contains(adjacent) && adjacent.within(perimeter));

            var stack = new Stack<Coord>();
            var visited = new HashSet<Coord>();
            stack.add(origin);
            visited.add(origin);

            while (!stack.isEmpty()) {
                var coord = stack.pop();
                coord.adjacents().forEach(adjacent -> {
                    if (coords.contains(adjacent) || !adjacent.within(perimeter)) {
                        return;
                    }
                    if (visited.add(adjacent)) {
                        stack.push(adjacent);
                        result.add(adjacent);
                    }
                });
            }

            return result;
        }
    }

    static Reader puzzleInput() {
        return new InputStreamReader(Day18.class.getResourceAsStream(INPUT));
    }

    static Set<Coord> parseInput(Reader reader) throws IOException {
        var coords = new HashSet<Coord>();
        try (var br = new BufferedReader(reader)) {
            br.lines().forEach(line -> {
                var parts = line.split(",");
                var x = Integer.parseInt(parts[0]);
                var y = Integer.parseInt(parts[1]);
                var z = Integer.parseInt(parts[2]);
                var coord = new Coord(x, y, z);
                coords.add(coord);
            });
        }
        return coords;
    }

    static int part1(Reader reader) throws IOException {
        var coords = parseInput(reader);
        return (int) coords.stream().flatMap(Coord::adjacents).filter(coord -> {
            return !coords.contains(coord);
        }).count();
    }

    static int part2(Reader reader) throws IOException {
        var coords = parseInput(reader);
        var reachable = Coord.reachableByWater(coords);
        return (int) coords.stream().flatMap(Coord::adjacents).filter(reachable::contains).count();
    }
}

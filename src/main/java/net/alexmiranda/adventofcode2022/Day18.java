package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day18 {
    private static final String INPUT = "/2022/day/18/input";

    record Coord(int x, int y, int z) {
        Stream<Coord> neighbours() {
            return IntStream.of(-1, 1)
                .boxed()
                .flatMap(i -> Stream.of(new Coord(x + i, y, z), new Coord(x, y + i, z), new Coord(x, y, z + i)));
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
        return (int) coords.stream().flatMap(Coord::neighbours).filter(coord -> {
            return !coords.contains(coord);
        }).count();
    }
    
}

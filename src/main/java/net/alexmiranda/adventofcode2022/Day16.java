package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Day16 {
    static final String INPUT = "/2022/Day/16/input";

    record Valve(int seq, String id, int flowRate, String[] leadsTo) {
    }

    static class Network {
        private static final Pattern PATTERN = Pattern.compile(
                "^Valve (?<id>[^\\s]{2}) has flow rate=(?<flowRate>\\d+); tunnel(s)* lead(s)* to valve(s)* (?<leadsTo>[^$]+)$");
        private final HashMap<String, Valve> valves = new HashMap<>((int) Math.ceil(64 / .75f), .75f);
        private int seq = -1;
        long allOpenFlag = 0;

        void parseInput(Reader reader) throws IOException {
            try (var br = new BufferedReader(reader)) {
                br.lines().forEach(this::parseLine);
            }
        }

        void parseLine(String line) {
            var m = PATTERN.matcher(line);
            if (m.matches()) {
                var id = m.group("id");
                var flowRate = Integer.parseInt(m.group("flowRate"));
                var leadsTo = m.group("leadsTo").split(", ");
                addValve(id, flowRate, leadsTo);
            }
        }

        void addValve(String id, int flowRate, String[] leadsTo) {
            valves.put(id, new Valve(++seq, id, flowRate, leadsTo));
            allOpenFlag |= 1L << seq;
            assert seq < 64;
        }

        int findMostPressurePossibleToRelease(String startingValve, int minutes) {
            return findMostPressurePossibleToRelease(0, startingValve, minutes - 1, 0L, new HashMap<>());
        }

        int findMostPressurePossibleToRelease(int pressureReleased, String valveId, int remainingMinutes,
                long openValves, HashMap<String, Integer> cache) {
            if (remainingMinutes == 0 || (openValves & allOpenFlag) == allOpenFlag) {
                return pressureReleased;
            }

            var cacheKey = String.format("%s%02d%x", valveId, remainingMinutes, openValves);
            var cached = cache.get(cacheKey);
            if (cached != null) {
                return cached;
            }

            var valve = valves.get(valveId);
            assert valve != null;

            int max = pressureReleased;
            if (remainingMinutes > 1 && valve.flowRate > 0 && (openValves & (1L << valve.seq)) == 0) {
                var pressure = valve.flowRate * remainingMinutes;
                assert pressure > 0;

                var pressureReleasedIncludingNewlyOpened = pressureReleased + pressure;
                if (remainingMinutes == 2) {
                    return pressureReleasedIncludingNewlyOpened;
                }

                var open = openValves | (1L << valve.seq);
                var remaining = remainingMinutes - 2;
                for (var next : valve.leadsTo) {
                    var result = findMostPressurePossibleToRelease(pressureReleasedIncludingNewlyOpened, next,
                            remaining, open, cache);
                    if (result > max) {
                        max = result;
                    }
                }
            }

            for (var next : valve.leadsTo) {
                var remaining = remainingMinutes - 1;
                var result = findMostPressurePossibleToRelease(pressureReleased, next, remaining, openValves, cache);
                if (result > max) {
                    max = result;
                }
            }

            cache.put(cacheKey, max);
            return max;
        }

        long computeOpenValves(String s) {
            long openValves = 0L;
            if (s.isEmpty()) {
                return openValves;
            }

            var ids = s.split(",");
            for (var id : ids) {
                var valve = valves.get(id);
                assert valve != null;
                openValves |= 1L << valve.seq;
            }
            return openValves;
        }
    }

    static Network parseInput(Reader reader) throws IOException {
        var network = new Network();
        network.parseInput(reader);
        return network;
    }

    public static void main(String[] args) {
        try (var reader = getPuzzleInput()) {
            var network = new Network();
            network.parseInput(reader);
            int result = network.findMostPressurePossibleToRelease("AA", 30);
            System.out.print("result: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static InputStreamReader getPuzzleInput() {
        return new InputStreamReader(Day16.class.getResourceAsStream(INPUT));
    }
}

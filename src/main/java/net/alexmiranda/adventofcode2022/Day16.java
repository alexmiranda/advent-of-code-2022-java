package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.regex.Pattern;

public class Day16 {
    static final String INPUT = "/2022/Day/16/input";

    record Valve(long id, String name, int flowRate, String[] leadsTo) {
    }

    record Route(String from, String to) {
    }

    static class Network {
        private static final Pattern PATTERN = Pattern.compile(
                "^Valve (?<name>[^\\s]{2}) has flow rate=(?<flowRate>\\d+); tunnel(s)* lead(s)* to valve(s)* (?<leadsTo>[^$]+)$");
        private final HashMap<String, Valve> valves = new HashMap<>((int) Math.ceil(64 / .75f), .75f);
        private final HashSet<String> workingValves = new HashSet<>((int) Math.ceil(16 / .75f), .75f);
        private int seq = 0;
        long allOpenFlag = 0;

        void parseInput(Reader reader) throws IOException {
            try (var br = new BufferedReader(reader)) {
                br.lines().forEach(this::parseLine);
            }
        }

        void parseLine(String line) {
            var m = PATTERN.matcher(line);
            if (m.matches()) {
                var name = m.group("name");
                var flowRate = Integer.parseInt(m.group("flowRate"));
                var leadsTo = m.group("leadsTo").split(", ");
                addValve(name, flowRate, leadsTo);
            }
        }

        void addValve(String name, int flowRate, String[] leadsTo) {
            assert seq < 64;
            long id = 1L << seq;
            valves.put(name, new Valve(id, name, flowRate, leadsTo));
            if (flowRate > 0) {
                workingValves.add(name);
            }
            allOpenFlag |= id;
            seq++;
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
            if (remainingMinutes > 1 && valve.flowRate > 0 && (openValves & valve.id) == 0) {
                var pressure = valve.flowRate * remainingMinutes;
                assert pressure > 0;

                var pressureReleasedIncludingNewlyOpened = pressureReleased + pressure;
                if (remainingMinutes == 2) {
                    return pressureReleasedIncludingNewlyOpened;
                }

                var open = openValves | valve.id;
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

        int quickReleaseMostPressureWithAnElephant(String startingValve, int minutes) {
            var distances = computeDistances(startingValve);
            var greatestReleasedPressure = new HashMap<Long, Integer>();

            record Iteration(String current, long openValves, int minutesLeft, int pressureReleased) {
            }
            var queue = new ArrayDeque<Iteration>();
            queue.add(new Iteration(startingValve, 0L, minutes, 0));

            while (!queue.isEmpty()) {
                var it = queue.poll();

                // stores the maximum possible pressure released for a given combination of open
                // valves
                greatestReleasedPressure.compute(it.openValves, (open, pressure) -> {
                    if (pressure == null) {
                        return it.pressureReleased;
                    }
                    return Math.max(pressure, it.pressureReleased);
                });

                // no time left or all valves are already open
                if (it.minutesLeft == 0 || (allOpenFlag & it.openValves) == allOpenFlag) {
                    continue;
                }

                var unopenedValves = unopenedValves(it.openValves);
                for (var valve : unopenedValves) {
                    // additional +1 cost due to time taken to open a valve
                    var cost = distances.get(new Route(it.current, valve.name)) + 1;

                    // no time left to open this valve
                    if (it.minutesLeft < cost) {
                        continue;
                    }

                    var openValves = it.openValves | valve.id;
                    var minutesLeft = it.minutesLeft - cost;
                    var pressureReleased = it.pressureReleased + minutesLeft * valve.flowRate;
                    var next = new Iteration(valve.name, openValves, minutesLeft, pressureReleased);
                    queue.add(next);
                }
            }

            // take all possibilities of resulting open valves and find the greatest pair
            // thereof where valves open on the left and right side are mutualy exclusive,
            // i.e. open by either of the two participants
            var possibilities = greatestReleasedPressure.keySet().toArray(size -> new Long[size]);
            int max = 0;
            for (int i = 0; i < possibilities.length - 1; i++) {
                var lhs = possibilities[i];
                for (int j = i + 1; j < possibilities.length; j++) {
                    var rhs = possibilities[j];
                    if ((lhs & rhs) == 0) {
                        int sum = greatestReleasedPressure.get(lhs) + greatestReleasedPressure.get(rhs);
                        if (sum > max) {
                            max = sum;
                        }
                    }
                }
            }

            return max;
        }

        private List<Valve> unopenedValves(long openValves) {
            var list = new LinkedList<Valve>();
            for (var name : workingValves) {
                var valve = valves.get(name);
                if ((~openValves & valve.id) == valve.id) {
                    list.add(valve);
                }
            }
            return list;
        }

        Optional<Integer> distanceBetween(String from, String to) {
            record Node(String valveId, int cost) implements Comparable<Node> {
                @Override
                public int compareTo(Node other) {
                    return this.cost - other.cost;
                }
            }
            var queue = new PriorityQueue<Node>();
            long seen = 0L;
            Valve valve = valves.get(from);
            queue.add(new Node(from, 0));
            seen |= valve.id;

            Valve neighbour = null;
            while (!queue.isEmpty()) {
                var node = queue.poll();
                if (node.valveId.equals(to)) {
                    return Optional.of(node.cost);
                }
                valve = valves.get(node.valveId);
                for (var next : valve.leadsTo) {
                    neighbour = valves.get(next);
                    if ((seen & neighbour.id) == 0) {
                        queue.add(new Node(next, node.cost + 1));
                        seen |= neighbour.id;
                    }
                }
            }

            return Optional.empty();
            // throw new RuntimeException("Valve " + to + " is not reachable from " + from);
        }

        private Map<Route, Integer> computeDistances(String startingValve) {
            var size = workingValves.size();
            var distances = new HashMap<Route, Integer>((int) Math.ceil(size * (size + 1) / .75f), .75f);
            for (var lhs : workingValves) {
                var dist = distanceBetween(startingValve, lhs);
                dist.ifPresent(distFromStart -> {
                    distances.put(new Route(startingValve, lhs), distFromStart);
                    for (var rhs : workingValves) {
                        if (rhs.equals(lhs)) {
                            continue;
                        }
                        var computedBefore = distances.get(new Route(rhs, lhs));
                        if (computedBefore != null) {
                            distances.put(new Route(lhs, rhs), computedBefore);
                            continue;
                        }
                        var distance = distanceBetween(lhs, rhs);
                        distance.ifPresent(d -> distances.put(new Route(lhs, rhs), d));
                    }
                });
            }
            return distances;
        }

        long computeOpenValves(String s) {
            long openValves = 0L;
            if (s.isEmpty()) {
                return openValves;
            }

            var names = s.split(",");
            for (var name : names) {
                var valve = valves.get(name);
                assert valve != null;
                openValves |= valve.id;
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
            System.out.println("--- PART 1 ---");
            System.out.println("answer: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (var reader = getPuzzleInput()) {
            var network = new Network();
            network.parseInput(reader);
            int result = network.quickReleaseMostPressureWithAnElephant("AA", 26);
            System.out.println("--- PART 2 ---");
            System.out.println("answer: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static InputStreamReader getPuzzleInput() {
        return new InputStreamReader(Day16.class.getResourceAsStream(INPUT));
    }
}

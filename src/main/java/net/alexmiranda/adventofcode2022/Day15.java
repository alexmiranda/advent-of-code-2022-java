package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;

public class Day15 {
    private static final String INPUT = "/2022/Day/15/input";

    record Location(int x, int y) {
    };

    record Sensor(int x, int y, int distClosestBeacon) {
    }

    static class TunnelNetwork {
        private final HashSet<Location> occupiedLocations = new HashSet<Location>(35);
        private final HashSet<Sensor> sensors = new HashSet<>(35);

        void parseInput(Reader reader) throws IOException {
            try (var br = new BufferedReader(reader)) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    parts[0] = parts[0].substring(parts[0].lastIndexOf("x="));
                    parts[1] = parts[1].substring(parts[1].lastIndexOf("x="));
                    var sx = Integer.parseInt(parts[0].substring(2, parts[0].indexOf(",")));
                    var sy = Integer.parseInt(parts[0].substring(parts[0].lastIndexOf("=") + 1));
                    var bx = Integer.parseInt(parts[1].substring(2, parts[1].indexOf(",")));
                    var by = Integer.parseInt(parts[1].substring(parts[1].lastIndexOf("=") + 1));
                    var sensor = new Sensor(sx, sy, distance(sx, sy, bx, by));
                    occupiedLocations.add(new Location(sx, sy));
                    occupiedLocations.add(new Location(bx, by));
                    sensors.add(sensor);
                }
            }
        }

        int tryDeployBeacon(int row) {
            var unfeasible = unfeasibleLocations(row);
            return unfeasible.size();
        }

        private HashSet<Integer> unfeasibleLocations(int row) {
            var unfeasible = new HashSet<Integer>();
            for (var sensor : sensors) {
                var ydist = Math.abs(sensor.y - row);
                if (ydist > sensor.distClosestBeacon) {
                    continue;
                }

                var start = sensor.x - (sensor.distClosestBeacon - ydist);
                var end = sensor.x + (sensor.distClosestBeacon - ydist);
                for (int i = start; i <= end; i++) {
                    if (unfeasible.contains(i) || occupiedLocations.contains(new Location(i, row))) {
                        continue;
                    }
                    if (distance(sensor.x, sensor.y, i, row) <= sensor.distClosestBeacon) {
                        unfeasible.add(i);
                    }
                }
            }
            return unfeasible;
        }

        long findTunningFreq() {
            int foundX = -1, foundY = -1;
            sensorsLoop: for (var sensor : sensors) {
                for (int y = 0; y <= sensor.distClosestBeacon; y++) {
                    var upperLeftX = sensor.x - sensor.distClosestBeacon - y - 1;
                    var upperLeftY = sensor.y - y;
                    if (checkBeaconLocation(upperLeftX, upperLeftY)) {
                        foundX = upperLeftX;
                        foundY = upperLeftY;
                        break sensorsLoop;
                    }
                    var upperRightX = sensor.x + sensor.distClosestBeacon - y + 1;
                    var upperRightY = sensor.y - y;
                    if (checkBeaconLocation(upperRightX, upperRightY)) {
                        foundX = upperRightX;
                        foundY = upperRightY;
                        break sensorsLoop;
                    }
                    var bottomRightX = sensor.x + sensor.distClosestBeacon - y + 1;
                    var bottomRightY = sensor.y + y;
                    if (checkBeaconLocation(bottomRightX, bottomRightY)) {
                        foundX = bottomRightX;
                        foundY = bottomRightY;
                        break sensorsLoop;
                    }
                    var bottomLeftX = sensor.x - sensor.distClosestBeacon - y - 1;
                    var bottomLeftY = sensor.y + y;
                    if (checkBeaconLocation(bottomLeftX, bottomLeftY)) {
                        foundX = bottomLeftX;
                        foundY = bottomLeftY;
                        break sensorsLoop;
                    }
                }
            }
            return (long) foundX * 4_000_000 + foundY;
        }

        boolean checkBeaconLocation(int x, int y) {
            if (x < 0 || y < 0 || x > 4_000_000 || y > 4_000_000) {
                return false;
            }
            for (var sensor : sensors) {
                if (distance(sensor.x, sensor.y, x, y) <= sensor.distClosestBeacon) {
                    return false;
                }
            }
            return true;
        }

        int distance(int x1, int y1, int x2, int y2) {
            return Math.abs(x2 - x1) + Math.abs(y2 - y1);
        }
    }

    static int countLocationsUnsuitableForBeacons(Reader reader, int row) throws IOException {
        var network = new TunnelNetwork();
        network.parseInput(reader);
        return network.tryDeployBeacon(row);
    }

    static int countLocationsUnsuitableForBeacons(int row) throws IOException {
        try (var reader = new InputStreamReader(Day15.class.getResourceAsStream(INPUT))) {
            var network = new TunnelNetwork();
            network.parseInput(reader);
            return network.tryDeployBeacon(row);
        }
    }

    static long getTunningFrequency(Reader reader) throws IOException {
        var network = new TunnelNetwork();
        network.parseInput(reader);
        return network.findTunningFreq();
    }

    static long getTunningFrequency() throws IOException {
        try (var reader = new InputStreamReader(Day15.class.getResourceAsStream(INPUT))) {
            var network = new TunnelNetwork();
            network.parseInput(reader);
            return network.findTunningFreq();
        }
    }

}

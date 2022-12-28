package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class Day19 {
    private static final String INPUT = "/2022/day/19/input";

    record Blueprint(
            int id,
            int orePerOreRobot,
            int orePerClayRobot,
            int orePerObsidianRobot,
            int clayPerObsidianRobot,
            int orePerGeodeRobot,
            int obsidianPerGeodeRobot) {
    }

    record Inventory(
            int turnsLeft,
            int oreQty,
            int clayQty,
            int obsidianQty,
            int openGeodes,
            int oreRobots,
            int clayRobots,
            int obsidianRobots,
            int geodeRobots) {

        static Builder with() {
            return new Builder();
        }

        static Builder with(Inventory prototype) {
            var builder = new Builder();
            builder.turnsLeft = prototype.turnsLeft;
            builder.oreQty = prototype.oreQty;
            builder.clayQty = prototype.clayQty;
            builder.obsidianQty = prototype.obsidianQty;
            builder.openGeodes = prototype.openGeodes;
            builder.oreRobots = prototype.oreRobots;
            builder.clayRobots = prototype.clayRobots;
            builder.obsidianRobots = prototype.obsidianRobots;
            builder.geodeRobots = prototype.geodeRobots;
            return builder;
        }

        static class Builder {
            private int turnsLeft;
            private int oreQty;
            private int clayQty;
            private int obsidianQty;
            private int openGeodes;
            private int oreRobots;
            private int clayRobots;
            private int obsidianRobots;
            private int geodeRobots;

            Builder turnsLeft(int value) {
                this.turnsLeft = value;
                return this;
            }

            Builder oreQty(int value) {
                this.oreQty = value;
                return this;
            }

            Builder clayQty(int value) {
                this.clayQty = value;
                return this;
            }

            Builder obsidianQty(int value) {
                this.obsidianQty = value;
                return this;
            }

            Builder openGeodes(int value) {
                this.openGeodes = value;
                return this;
            }

            Builder oreRobots(int value) {
                this.oreRobots = value;
                return this;
            }

            Builder clayRobots(int value) {
                this.clayRobots = value;
                return this;
            }

            Builder obsidianRobots(int value) {
                this.obsidianRobots = value;
                return this;
            }

            Builder geodeRobots(int value) {
                this.geodeRobots = value;
                return this;
            }

            Inventory build() {
                return new Inventory(turnsLeft, oreQty, clayQty, obsidianQty, openGeodes, oreRobots, clayRobots,
                        obsidianRobots, geodeRobots);
            }
        }
    };

    static class Factory {
        private static final Pattern PATTERN = Pattern.compile(
                "^Blueprint (?<id>\\d+): Each ore robot costs (?<orePerOreRobot>\\d+) ore. Each clay robot costs (?<orePerClayRobot>\\d+) ore. Each obsidian robot costs (?<orePerObsidianRobot>\\d+) ore and (?<clayPerObsidianRobot>\\d+) clay. Each geode robot costs (?<orePerGeodeRobot>\\d+) ore and (?<obsidianPerGeodeRobot>\\d+) obsidian.$");

        final List<Blueprint> blueprints = new ArrayList<>(30);

        void readBlueprints(Reader reader) throws IOException {
            readBlueprints(reader, Integer.MAX_VALUE);
        }

        void readBlueprints(Reader reader, int n) throws IOException {
            try (BufferedReader br = new BufferedReader(reader)) {
                br.lines().limit(n).forEach(line -> {
                    var m = PATTERN.matcher(line);
                    if (m.matches()) {
                        int id = Integer.parseInt(m.group("id"));
                        int orePerOreRobot = Integer.parseInt(m.group("orePerOreRobot"));
                        int orePerClayRobot = Integer.parseInt(m.group("orePerClayRobot"));
                        int orePerObsidianRobot = Integer.parseInt(m.group("orePerObsidianRobot"));
                        int clayPerObsidianRobot = Integer.parseInt(m.group("clayPerObsidianRobot"));
                        int orePerGeodeRobot = Integer.parseInt(m.group("orePerGeodeRobot"));
                        int obsidianPerGeodeRobot = Integer.parseInt(m.group("obsidianPerGeodeRobot"));
                        var blueprint = new Blueprint(id, orePerOreRobot, orePerClayRobot, orePerObsidianRobot,
                                clayPerObsidianRobot, orePerGeodeRobot, obsidianPerGeodeRobot);
                        blueprints.add(blueprint);
                    }
                });
            }
        }

        int determineQualityLevel(int minutes) {
            return blueprints.stream()
                    .parallel()
                    .mapToInt(blueprint -> maximumOpenGeodes(blueprint, minutes) * blueprint.id())
                    .sum();
        }

        int multiplyMaximumOpenGeodes(int minutes) {
            return blueprints.stream()
                    .parallel()
                    .mapToInt(blueprint -> maximumOpenGeodes(blueprint, minutes))
                    .reduce((a, b) -> a * b)
                    .orElse(0);
        }

        int maximumOpenGeodes(Blueprint blueprint, int minutes) {
            int maxOpenGeodes = 0;
            var start = Inventory.with().oreRobots(1).turnsLeft(minutes).build();
            var queue = new ArrayDeque<Inventory>();
            queue.add(start);

            // we should never produce excess of ore
            int maxOreRobots = Math.max(Math.max(blueprint.orePerClayRobot, blueprint.orePerGeodeRobot),
                    blueprint.orePerObsidianRobot);
            assert maxOreRobots > 0;

            // we should never produce excess of clay
            int maxClayRobots = blueprint.clayPerObsidianRobot;
            assert maxClayRobots > 0;

            // we should never produce excess of obsidian
            int maxObsidianRobots = blueprint.obsidianPerGeodeRobot;
            assert maxObsidianRobots > 0;

            var seen = new HashSet<Integer>();
            while (!queue.isEmpty()) {
                var inventory = queue.poll();
                if (!seen.add(inventory.hashCode())) {
                    continue;
                }

                maxOpenGeodes = Math.max(maxOpenGeodes, inventory.openGeodes);
                if (inventory.turnsLeft == 0) {
                    continue;
                }

                // if we know that producing geodes robots and opening geodes every single turn
                // until the end of the simulation cannot possibly beat the already highest
                // number of open geodes, then we can simply skip it
                if (inventory.openGeodes + (inventory.turnsLeft * inventory.geodeRobots)
                        + (inventory.turnsLeft * (inventory.turnsLeft + 1) / 2) <= maxOpenGeodes) {
                    continue;
                }

                int oreQty = inventory.oreQty + inventory.oreRobots;
                int clayQty = inventory.clayQty + inventory.clayRobots;
                int obsidianQty = inventory.obsidianQty + inventory.obsidianRobots;
                int openGeodes = inventory.openGeodes + inventory.geodeRobots;
                int canCreateRobots = 0;

                // per turn the factory can *only* produce a single robot of any kind
                // if possible, we create a new geode robot
                if (inventory.oreQty >= blueprint.orePerGeodeRobot
                        && inventory.obsidianQty >= blueprint.obsidianPerGeodeRobot
                        && inventory.turnsLeft >= 2) {
                    queue.add(Inventory.with(inventory)
                            .turnsLeft(inventory.turnsLeft - 1)
                            .oreQty(oreQty - blueprint.orePerGeodeRobot)
                            .clayQty(clayQty)
                            .obsidianQty(obsidianQty - blueprint.obsidianPerGeodeRobot)
                            .geodeRobots(inventory.geodeRobots + 1)
                            .openGeodes(openGeodes)
                            .build());

                    // if we can create a geode robot, we don't care about the other branches...
                    continue;
                }

                // if possible, we create a new ore robot
                if (inventory.oreQty >= blueprint.orePerOreRobot
                        && inventory.oreRobots < maxOreRobots) {
                    queue.add(Inventory.with(inventory)
                            .turnsLeft(inventory.turnsLeft - 1)
                            .oreQty(oreQty - blueprint.orePerOreRobot)
                            .clayQty(clayQty)
                            .obsidianQty(obsidianQty)
                            .oreRobots(inventory.oreRobots + 1)
                            .openGeodes(openGeodes)
                            .build());
                    canCreateRobots++;
                }

                // if possible, we create a new clay robot
                if (inventory.oreQty >= blueprint.orePerClayRobot
                        && inventory.clayRobots < maxClayRobots
                        && inventory.turnsLeft >= 6) {
                    queue.add(Inventory.with(inventory)
                            .turnsLeft(inventory.turnsLeft - 1)
                            .oreQty(oreQty - blueprint.orePerClayRobot)
                            .clayQty(clayQty)
                            .obsidianQty(obsidianQty)
                            .clayRobots(inventory.clayRobots + 1)
                            .openGeodes(openGeodes)
                            .build());
                    canCreateRobots++;
                }

                // if possible, we create a new obsidian robot
                if (inventory.oreQty >= blueprint.orePerObsidianRobot
                        && inventory.clayQty >= blueprint.clayPerObsidianRobot
                        && inventory.turnsLeft >= 4 && inventory.obsidianRobots < maxObsidianRobots) {
                    queue.add(Inventory.with(inventory)
                            .turnsLeft(inventory.turnsLeft - 1)
                            .oreQty(oreQty - blueprint.orePerObsidianRobot)
                            .clayQty(clayQty - blueprint.clayPerObsidianRobot)
                            .obsidianQty(obsidianQty)
                            .obsidianRobots(inventory.obsidianRobots + 1)
                            .openGeodes(openGeodes)
                            .build());
                    canCreateRobots++;
                }

                // there's no sense in waiting if we can create any robot want
                if (minutes <= 24 || canCreateRobots < 3) {
                    queue.add(Inventory.with(inventory)
                            .turnsLeft(inventory.turnsLeft - 1)
                            .oreQty(oreQty)
                            .clayQty(clayQty)
                            .obsidianQty(obsidianQty)
                            .openGeodes(openGeodes)
                            .build());
                }
            }

            return maxOpenGeodes;
        }
    }

    static Reader puzzleInput() {
        return new InputStreamReader(Day19.class.getResourceAsStream(INPUT));
    }
}

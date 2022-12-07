package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class Day7 {
    private static final String INPUT = "2022/Day/7/input";

    sealed interface Entry permits File, Directory {
        String name();

        int size();

        Directory parent();
    }

    final record File(String name, int size, Directory parent) implements Entry {
    }

    static final class Directory implements Entry {
        private final String name;
        private final Directory parent;
        private final Map<String, Entry> children = new HashMap<>();

        Directory(String name, Directory parent) {
            this.name = name;
            this.parent = parent;
        }

        @Override
        public int size() {
            return children.values().stream().mapToInt(Entry::size).sum();
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public Directory parent() {
            return this.parent;
        }

        Directory dir(String name) {
            if (children.get(name) instanceof Directory dir) {
                return dir;
            }
            throw new IllegalArgumentException(name);
        }

        void add(Entry entry) {
            this.children.put(entry.name(), entry);
        }
    }

    public static Directory parseTree(Reader reader) throws IOException {
        Directory root = new Directory("/", null);
        try (var br = new BufferedReader(reader)) {
            Directory cwd = null;
            String line = null;

            boolean listing = false;
            while ((line = br.readLine()) != null) {
                try (var scanner = new Scanner(line)) {
                    scanner.useDelimiter(" ");
                    boolean done = false;
                    while (!done) {
                        String token;
                        switch (token = scanner.next()) {
                            case "$": {
                                listing = false;
                                continue;
                            }
                            case "cd": {
                                var dirname = scanner.next();
                                cwd = switch (dirname) {
                                    case "/" -> root;
                                    case ".." -> cwd.parent();
                                    default -> cwd.dir(dirname);
                                };
                                done = true;
                                break;
                            }
                            case "ls": {
                                assert !scanner.hasNext() : "ls command with arguments";
                                listing = true;
                                done = true;
                                break;
                            }
                            case "dir": {
                                assert listing : "directory listing without prior ls command";
                                var dirname = scanner.next();
                                cwd.add(new Directory(dirname, cwd));
                                done = true;
                                break;
                            }
                            default: {
                                assert listing : "expected to be listing directory";
                                var size = Integer.parseInt(token);
                                var filename = scanner.next();
                                cwd.add(new File(filename, size, cwd));
                                done = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return root;
    }

    public static int part1(Reader reader) throws IOException {
        var tree = parseTree(reader);
        return sumDirectoriesSmallerThan(tree, 100000);
    }

    public static int part1() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        var reader = Files.newBufferedReader(path);
        return part1(reader);
    }

    public static int part2(Reader reader) throws IOException {
        var diskSpace = 70000000;
        var requiredDiskSpace = 30000000;
        var tree = parseTree(reader);
        var usedDiskSpace = tree.size();
        var availableDiskSpace = diskSpace - usedDiskSpace;
        return findBestCandidateForDeletion(tree, Math.abs(requiredDiskSpace - availableDiskSpace));
    }

    public static int part2() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        var reader = Files.newBufferedReader(path);
        return part2(reader);
    }

    public static int sumDirectoriesSmallerThan(Directory dir, int limit) {
        int sum = 0;
        int size = dir.size();
        if (size <= limit) {
            sum += size;
        }
        for (var child : dir.children.values()) {
            if (child instanceof Directory childDir) {
                sum += sumDirectoriesSmallerThan(childDir, limit);
            }
        }
        return sum;
    }

    public static int findBestCandidateForDeletion(Directory tree, int limit) {
        var queue = new LinkedList<Directory>();
        queue.add(tree);
        int smallest = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            var dir = queue.poll();
            var size = dir.size();
            if (size == limit) {
                return size;
            }
            if (size > limit) {
                smallest = Math.min(smallest, size);
                for (var child : dir.children.values()) {
                    if (child instanceof Directory next) {
                        queue.offer(next);
                    }
                }
            }
        }

        return smallest;
    }
}

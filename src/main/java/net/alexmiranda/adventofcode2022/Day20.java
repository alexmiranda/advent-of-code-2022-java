package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day20 {
    static final long DECRYPTION_KEY = 811589153L;

    private static final String INPUT = "/2022/day/20/input";
    private static final int[] GROVE_COORDS = new int[] { 1000, 2000, 3000 };

    record Number(long longValue, int originalPosition) {
        @Override
        public String toString() {
            return Long.toString(longValue);
        }
    }

    static long sumOfGroveCoordinates(List<Long> list) {
        int zeroPos = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(0L)) {
                zeroPos = i;
                break;
            }
        }

        assert zeroPos >= 0;

        var sum = 0L;
        for (var pos : GROVE_COORDS) {
            int actualPos = (zeroPos + pos) % list.size();
            sum += list.get(actualPos);
        }
        return sum;
    }

    static List<Long> mix(List<Long> file, int rounds) {
        int n = file.size();
        var input = new ArrayList<Number>(n);
        for (int i = 0; i < n; i++) {
            input.add(new Number(file.get(i), i));
        }

        while (rounds > 0) {
            rounds--;

            for (int i = 0; i < n; i++) {
                var from = input.indexOf(new Number(file.get(i), i));
                var num = input.get(from);
                if (num.longValue == 0) {
                    continue;
                }

                int to = targetPosition(from, num.longValue, n);
                if (from == to) {
                    continue;
                }

                var small = Math.min(from, to);
                var big = Math.max(from, to);
                Collections.rotate(input.subList(small, big + 1), from < to ? -1 : 1);
            }
        }

        return input.stream().mapToLong(v -> v.longValue).boxed().toList();
    }

    static int targetPosition(int pos, long value, int n) {
        long dest = pos + value;
        if (dest >= n) {
            dest %= n - 1;
        } else if (dest < 0) {
            dest %= n - 1;
            dest += n - 1;
        }
        return (int) dest;
    }

    static List<Long> mix(Reader reader, int n, int rounds, long decryptionKey) throws IOException {
        try (var br = new BufferedReader(reader)) {
            var file = new ArrayList<Long>(n);
            br.lines().limit(n).mapToLong(Long::parseLong)
                    .map(longValue -> Math.multiplyExact(longValue, decryptionKey)).forEach(file::add);
            return mix(Collections.unmodifiableList(file), rounds);
        }
    }

    static List<Long> applyDecryptionKey(List<Long> list) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i) * DECRYPTION_KEY);
        }
        return list;
    }

    static InputStreamReader puzzleInput() {
        return new InputStreamReader(Day20.class.getResourceAsStream(INPUT));
    }
}

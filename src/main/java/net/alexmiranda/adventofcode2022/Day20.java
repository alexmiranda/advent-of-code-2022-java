package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day20 {
    private static final String INPUT = "/2022/day/20/input";
    private static final int[] GROVE_COORDS = new int[] { 1000, 2000, 3000 };

    record Number(int intValue, int originalPosition) {
        @Override
        public String toString() {
            return Integer.toString(intValue);
        }
    }

    static int answerPart1(List<Integer> list) {
        int zeroPos = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).intValue() == 0) {
                zeroPos = i;
                break;
            }
        }

        assert zeroPos >= 0;

        var sum = 0;
        for (var pos : GROVE_COORDS) {
            int actualPos = (zeroPos + pos) % list.size();
            sum += list.get(actualPos);
        }
        return sum;
    }

    static List<Integer> mix(List<Integer> file) {
        int n = file.size();
        var input = new ArrayList<Number>(n);
        for (int i = 0; i < n; i++) {
            input.add(new Number(file.get(i), i));
        }

        for (int i = 0; i < n; i++) {
            var from = input.indexOf(new Number(file.get(i), i));
            var num = input.get(from);
            if (num.intValue == 0) {
                continue;
            }

            int to = targetPosition(from, num.intValue, n);
            if (from == to) {
                continue;
            }

            var small = Math.min(from, to);
            var big = Math.max(from, to);
            Collections.rotate(input.subList(small, big + 1), from < to ? -1 : 1);
        }

        return input.stream().mapToInt(v -> v.intValue).boxed().toList();
    }

    static int targetPosition(int pos, int value, int n) {
        int dest = pos + value;
        if (dest >= n) {
            dest %= n - 1;
        } else if (dest < 0) {
            dest %= n - 1;
            dest += n - 1;
        }
        return dest;
    }

    static List<Integer> mix(Reader reader, int n) throws IOException {
        try (var br = new BufferedReader(reader)) {
            var file = new ArrayList<Integer>(n);
            br.lines().limit(n).mapToInt(Integer::parseInt).forEach(file::add);
            return mix(file);
        }
    }

    static InputStreamReader puzzleInput() {
        return new InputStreamReader(Day20.class.getResourceAsStream(INPUT));
    }
}

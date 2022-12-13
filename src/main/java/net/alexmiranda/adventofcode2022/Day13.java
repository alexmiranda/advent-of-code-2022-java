package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;
import java.util.stream.IntStream;

public class Day13 {
    private static final String INPUT = "/2022/Day/13/input";

    sealed interface Packet extends Comparable<Packet> permits Single, Multi, Empty {
        static final Empty Empty = new Empty();

        static Packet fromString(String s) {
            var elem = "";
            Packet packet = null;
            var stack = new Stack<Packet>();

            for (char c : s.toCharArray()) {
                switch (c) {
                    case '[': {
                        if (packet != null) {
                            stack.push(packet);
                        }
                        packet = Empty;
                        break;
                    }
                    case ']': {
                        if (!elem.isEmpty()) {
                            var i = Integer.valueOf(elem);
                            packet = packet.append(i);
                            elem = "";
                        }

                        if (stack.isEmpty()) {
                            continue;
                        }

                        var prev = stack.pop();
                        packet = prev.append(packet);
                        break;
                    }
                    case ',': {
                        if (elem.isEmpty()) {
                            continue;
                        }

                        var i = Integer.valueOf(elem);
                        packet = packet.append(i);
                        elem = "";
                        break;
                    }
                    default: {
                        assert Character.isDigit(c);
                        elem += c;
                        break;
                    }
                }
            }

            return packet;
        }

        default Packet append(int val) {
            return this.append(new Single(val));
        }

        Packet append(Packet packet);
    };

    record Single(int val) implements Packet {
        @Override
        public int compareTo(Packet right) {
            if (right instanceof Single s) {
                return this.val - s.val;
            } else if (right instanceof Multi m) {
                return this.asMulti().compareTo(m);
            } else if (right instanceof Empty e) {
                return -e.compareTo(this);
            }
            throw new RuntimeException("non-exhaustive type check...");
        }

        private Multi asMulti() {
            return new Multi(this);
        }

        @Override
        public Packet append(Packet packet) {
            throw new RuntimeException("Cannot append to Single packet");
        }

        @Override
        public String toString() {
            return Integer.toString(val);
        }
    };

    record Multi(Packet... packets) implements Packet {
        @Override
        public int compareTo(Packet right) {
            if (right instanceof Single s) {
                return -s.compareTo(this);
            } else if (right instanceof Empty e) {
                return -e.compareTo(this);
            } else if (right instanceof Multi m) {
                var minLength = Math.min(this.packets.length, m.packets.length);
                for (int i = 0; i < minLength; i++) {
                    var cmp = packets[i].compareTo(m.packets[i]);
                    if (cmp != 0) {
                        return cmp;
                    }
                }
                return packets.length - m.packets.length;
            }

            throw new RuntimeException("non-exhaustive type check...");
        }

        @Override
        public Packet append(Packet packet) {
            var packets = new Packet[this.packets.length + 1];
            System.arraycopy(this.packets, 0, packets, 0, packets.length - 1);
            packets[packets.length - 1] = packet;
            return new Multi(packets);
        }

        @Override
        public String toString() {
            var sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < packets.length - 1; i++) {
                sb.append(packets[i]);
                sb.append(',');
            }
            sb.append(packets[packets.length - 1]);
            sb.append(']');
            return sb.toString();
        }
    };

    record Empty() implements Packet {
        @Override
        public int compareTo(Packet right) {
            if (right instanceof Empty) {
                return 0;
            }
            return -1;
        }

        @Override
        public Packet append(Packet packet) {
            return new Multi(packet);
        }

        @Override
        public String toString() {
            return "[]";
        }
    }

    static int countPairsInRightOrder() throws IOException {
        try (var reader = readInput()) {
            return countPairsInRightOrder(reader);
        }
    }

    static int countPairsInRightOrder(Reader reader) throws IOException {
        Packet[] pair = new Packet[2];
        try (var br = new BufferedReader(reader)) {
            String line = null;
            int i = 0, pairNum = 0, counter = 0;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    pairNum++;
                    var cmp = pair[0].compareTo(pair[1]);
                    if (cmp <= 0) {
                        counter += pairNum;
                    }
                    pair[0] = null;
                    pair[1] = null;
                    continue;
                }
                pair[i] = Packet.fromString(line);
                i = (i + 1) % 2;
            }

            if (pair[1] != null) {
                pairNum++;
                var cmp = pair[0].compareTo(pair[1]);
                if (cmp <= 0) {
                    counter += pairNum;
                }
            }

            return counter;
        }
    }

    static int decoderKeyOfDistressSignal(Packet... decoderKeys) throws IOException {
        try (var reader = readInput()) {
            return decoderKeyOfDistressSignal(reader, decoderKeys);
        }
    }
    
    static int decoderKeyOfDistressSignal(Reader reader, Packet... decoderKeys) throws IOException {
        var list = new ArrayList<Packet>(152);
        Collections.addAll(list, decoderKeys);
        try (var br = new BufferedReader(reader)) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                var packet = Packet.fromString(line);
                list.add(packet);
            }
            Collections.sort(list);
            return IntStream.range(1, list.size())
                .filter(i -> Arrays.binarySearch(decoderKeys, list.get(i - 1)) >= 0)
                .reduce(1, (a, b) -> a * b);
        }
    }

    static InputStreamReader readInput() {
        return new InputStreamReader(Day13.class.getResourceAsStream(INPUT));
    }
}

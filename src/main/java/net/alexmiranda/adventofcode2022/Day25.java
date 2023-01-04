package net.alexmiranda.adventofcode2022;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;

public class Day25 {
    private static final String INPUT = "/2022/day/25/input";

    static class SNAFU {
        static final SNAFU ZERO = new SNAFU("0", 0);

        private final String value;
        private final long longValue;

        private SNAFU(String value, long longValue) {
            this.value = value;
            this.longValue = longValue;
        }

        SNAFU add(SNAFU other) {
            return SNAFU.fromLong(Math.addExact(this.longValue, other.longValue));
        }

        static SNAFU fromInt(int intValue) {
            return fromLong(intValue);
        }

        static SNAFU fromLong(long longValue) {
            if (longValue == 0) {
                return ZERO;
            }

            var n = longValue;
            var sb = new StringBuilder();
            var digits = new char[] { '0', '1', '2', '=', '-' };
            while (n != 0) {
                var remainder = mod(n, 5);
                var nextDigit = digits[remainder];
                n = Math.addExact(n, 2) / 5;
                sb.insert(0, nextDigit);
            }
            return new SNAFU(sb.toString(), longValue);
        }

        static SNAFU fromString(String value) {
            long n = 0;
            for (var c : value.toCharArray()) {
                n = Math.multiplyExact(n, 5);
                n = Math.addExact(n, digitValue(c));
            }
            return new SNAFU(value, n);
        }

        private static int digitValue(char quoteQuoteDigit) {
            return switch (quoteQuoteDigit) {
                case '0', '1', '2' -> quoteQuoteDigit - '0';
                case '-' -> -1;
                case '=' -> -2;
                default -> throw new IllegalArgumentException("Invalid SNAFU digit: " + quoteQuoteDigit);
            };
        }

        long longValue() {
            return this.longValue;
        }

        @Override
        public String toString() {
            return this.value;
        }

        private static int mod(long dividend, int divisor) {
            var mod = (int) (dividend % divisor);
            if (mod < 0) {
                mod += divisor;
            }
            return mod;
        }
    }

    static SNAFU sum(Readable input) {
        var result = SNAFU.ZERO;
        try (var scanner = new Scanner(input)) {
            while (scanner.hasNextLine()) {
                result = result.add(SNAFU.fromString(scanner.nextLine()));
            }
        }
        return result;
    }

    static Reader puzzleInput() {
        return new InputStreamReader(Day25.class.getResourceAsStream(INPUT));
    }
}

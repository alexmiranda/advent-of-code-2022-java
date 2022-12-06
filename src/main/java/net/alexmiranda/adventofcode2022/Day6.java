package net.alexmiranda.adventofcode2022;

public class Day6 {
    public static int charactersBeforeStartOfNewPacket(String s) {
        return findMarker(s, 4);
    }

    public static int charactersBeforeStartOfNewMessage(String s) {
        return findMarker(s, 14);
    }

    private static int findMarker(String s, int n) {
        for (int i = n; i < s.length(); i++) {
            var received = s.substring(i - n, i);
            if (received.chars().distinct().count() == n) {
                return i;
            }
        }
        return s.length();
    }
}

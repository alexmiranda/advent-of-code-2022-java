package net.alexmiranda.adventofcode2022;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Day1 {
    private static final String INPUT = "2022/day/1/input";

    public static int calorieCounting() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        try (var reader = Files.newBufferedReader(path)) {
            String line = null;
            int max = 0, sum = 0;
            while ((line = reader.readLine()) != null) {
                if (line.equals("")) {
                    max = Math.max(max, sum);
                    sum = 0;
                    continue;
                }

                var i = Integer.parseInt(line);
                sum += i;
            }

            int answer = Math.max(max, sum);
            return answer;
        }
    }

    public static int calorieCountingTopThree() throws URISyntaxException, IOException {
        int[] topThree = new int[3];
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        try (var reader = Files.newBufferedReader(path)) {
            String line = null;
            int sum = 0;
            while ((line = reader.readLine()) != null) {
                if (line.equals("")) {
                    if (sum >= topThree[2]) {
                        topThree[0] = topThree[1];
                        topThree[1] = topThree[2];
                        topThree[2] = sum;
                    } else if (sum >= topThree[1]) {
                        topThree[0] = topThree[1];
                        topThree[1] = sum;
                    } else if (sum > topThree[0]) {
                        topThree[0] = sum;
                    }
                    sum = 0;
                    continue;
                }

                var i = Integer.parseInt(line);
                sum += i;
            }
        }
        ;

        return topThree[0] + topThree[1] + topThree[2];
    }
}

package net.alexmiranda.adventofcode2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Stack;

public class Day5 {
    private static final String INPUT = "2022/Day/5/input";

    public static Reader getInputFile() throws URISyntaxException, IOException {
        var path = Path.of(ClassLoader.getSystemResource(INPUT).toURI());
        return Files.newBufferedReader(path);
    }

    public static abstract class CrateMover<T extends Collection<? super Character>> {
        private final LinkedHashMap<Character, T> stacks = new LinkedHashMap<>(9);

        public void addStack(char id) {
            var stack = newStack();
            this.stacks.put(id, stack);
        }

        public void pushToStack(char id, char crate) {
            stacks.computeIfPresent(id, (_id, stack) -> {
                push(stack, crate);
                return stack;
            });
        }

        public Reader loadFromInputFile() throws URISyntaxException, IOException {
            var reader = Day5.getInputFile();
            load(reader);
            return reader;
        }

        public void load(Reader reader) throws IOException {
            var br = bufferedReader(reader);
            String line = null;
            var q = new ArrayDeque<char[]>(8);
            char[] arr = null;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0)
                    break;

                int len = (line.length() + 2) / 4;
                arr = new char[len];
                assert len <= 9 : "maximum 9 stacks limit exceeded: " + len;

                for (int i = 0, n = 1; n < line.length(); i++, n += 4) {
                    if (line.charAt(n) == ' ')
                        continue;
                    arr[i] = line.charAt(n);
                }
                q.push(arr);
            }

            var header = q.poll();
            for (int i = 0; i < header.length; i++) {
                this.addStack(header[i]);
            }

            while (!q.isEmpty()) {
                var row = q.poll();
                for (int i = 0; i < row.length; i++) {
                    if (row[i] != '\0') {
                        this.pushToStack(header[i], row[i]);
                    }
                }
            }
        }

        public void readInstructions(Reader reader) throws IOException {
            var br = bufferedReader(reader);
            String line = null;

            while ((line = br.readLine()) != null) {
                var tokens = line.split(" ", 6);
                assert tokens.length == 6 : "invalid instruction: " + line;

                int n = Integer.parseInt(tokens[1]);
                char from = tokens[3].charAt(0);
                char to = tokens[5].charAt(0);
                this.move(n, from, to);
            }
        }

        public CharSequence cratesOnTop() {
            var arr = new char[stacks.size()];
            int i = 0;
            for (var stack : stacks.values()) {
                arr[i++] = peek(stack);
            }
            return CharBuffer.wrap(arr);
        }

        public void move(int n, char from, char to) {
            var src = stacks.get(from);
            var dest = stacks.get(to);
            move(n, src, dest);
        }

        public abstract void move(int n, T from, T to);

        protected abstract T newStack();

        protected abstract void push(T stack, char crate);

        protected abstract char peek(T stack);

        // avoid problems with lines being skipped between loading stacks and reading
        // instructions
        private static BufferedReader bufferedReader(Reader reader) {
            if (reader instanceof BufferedReader br) {
                return br;
            }
            return new BufferedReader(reader);
        }
    }

    public static class CrateMover9000 extends CrateMover<Stack<Character>> {

        @Override
        public void move(int n, Stack<Character> from, Stack<Character> to) {
            assert n <= from.size() : "stack doesn't have enough cranes: " + n;
            for (int i = 0; i < n; i++) {
                to.push(from.pop());
            }
        }

        @Override
        protected Stack<Character> newStack() {
            return new Stack<>();
        }

        @Override
        protected void push(Stack<Character> stack, char crate) {
            stack.push(crate);
        }

        @Override
        protected char peek(Stack<Character> stack) {
            return stack.peek();
        }
    }

    public static class CrateMover9001 extends CrateMover<ArrayList<Character>> {
        @Override
        public void move(int n, ArrayList<Character> from, ArrayList<Character> to) {
            assert n <= from.size() : "stack doesn't have enough cranes: " + n;
            var sublist = from.subList(from.size() - n, from.size());
            to.addAll(sublist);
            sublist.clear();
        }

        @Override
        protected ArrayList<Character> newStack() {
            return new ArrayList<>(8);
        }

        @Override
        protected void push(ArrayList<Character> stack, char crate) {
            stack.add(crate);
        }

        @Override
        protected char peek(ArrayList<Character> stack) {
            return stack.get(stack.size() - 1);
        }
    }
}

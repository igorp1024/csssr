package csssr;

import java.util.*;
import java.util.stream.Collectors;

public class CsssrTest {

    public static void main(String[] args) {
        final String source = "   сапог   сарай  сапо    сбпо  спг       арбуз   биржаа    болт  бокс биржа    яяя я   ";
        System.out.println(new StreamSolution().solve(source));
        System.out.println(new LowLevelSolution().solve(source));
    }

    /**
     * A base class with input validation and utility stuff.
     */
    public abstract static class AbstractSolution {

        /**
         * Comparator which puts longer strings at the beginning and shorter at the end of the collection.
         * String of the same size are alphabetically sorted.
         */
        protected static final Comparator<String> COMPARATOR =
                (o1, o2) ->
                        (o1.length() == o2.length())
                                ? o1.compareTo(o2)
                                :
                                (o1.length() > o2.length())
                                        ? -1
                                        : 1;

        Map<String, Set<String>> solve(String str) {
            if (str == null || str.trim().length() == 0) {
                throw new IllegalArgumentException(
                        String.format(
                                "Source string is empty: \"%s\"", str
                        )
                );
            }
            return _solve(str.trim());
        }

        protected abstract Map<String, Set<String>> _solve(String str);
    }

    /**
     * A low-level solution, which is a bit more performant.
     */
    private static class LowLevelSolution extends AbstractSolution {

        @Override
        public Map<String, Set<String>> _solve(String str) {

            Map<String, Set<String>> result = new TreeMap<>();

            // Fill the map with values
            int tokenStart, curIdx = 0;
            while (curIdx < str.length()) {
                // Skip series of spaces (if there are any)
                if (str.charAt(curIdx) == ' ') {
                    curIdx++;
                    continue;
                }
                // Mark token start
                tokenStart = curIdx;
                // Mark token end
                curIdx = str.indexOf(' ', curIdx);
                if (curIdx == -1) {
                    curIdx = str.length();
                }
                // Pick the next string token value
                String nextString = str.substring(tokenStart, curIdx);
                // Define a key
                String key = nextString.substring(0, 1);
                // Initialize the "bucket" for group of values
                if (!result.containsKey(key)) {
                    result.put(key, new TreeSet<>(COMPARATOR));
                }
                // Store the value
                result.get(key).add(nextString);
            }

            // Filter out the inappropriate values (single-valued groups)
            Iterator<Map.Entry<String, Set<String>>> iterator = result.entrySet().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getValue().size() == 1) {
                    iterator.remove();
                }
            }

            return result;
        }
    }

    /**
     * A functional-style solution. A functional "one-liner".
     */
    private static class StreamSolution extends AbstractSolution {
        @Override
        public Map<String, Set<String>> _solve(String str) {
            return Arrays.stream(str.split("\\s+"))
                    .collect(
                            Collectors.groupingBy(
                                    v -> v.substring(0, 1),
                                    Collectors.toCollection(
                                            () -> {
                                                return new TreeSet<>(
                                                        COMPARATOR
                                                );
                                            }
                                    )
                            )
                    ).entrySet()
                    .stream()
                    .filter(e -> e.getValue().size() > 1)
                    .collect(
                            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, TreeMap::new)
                    );
        }
    }
}

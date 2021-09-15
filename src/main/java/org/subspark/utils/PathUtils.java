package org.subspark.utils;

import java.util.*;
import java.util.regex.Pattern;

public class PathUtils {
    /**
     * Each path pattern starts with a "/" and ends without "/".
     * Split a path pattern to parts with "/", each part satisfies
     * one of three situations below:
     * 1. it can be matched with "\w+". eg, "name", "student_name", "class1";
     * 2. it starts with a ":" and left parts satisfies 1, which stands for a named parameter;
     * 3. it is a "*", which stands for a wildcard.
     * 
     * Thus, "/*", "/:name", "/p1/:p2/p3/*" are valid path patterns.
     * "/*.jpg", "/p4/" are invalid path patterns.
     * 
     * Besides, there is a special case "/". It does not satisfy any situations above,
     * but it's a valid path pattern, standing for root path of the application.
     */
    private static final String PATH_PATTERN = "^(/|(/((:?\\w+)|\\*))+)";

    public static void verifyPathPattern(String pattern) {
        if (!Pattern.matches(PATH_PATTERN, pattern)) {
            throw new IllegalArgumentException(String.format("Path pattern %s is invalid", pattern));
        }
    }

    private static ArrayList<String> splitPath(String path) {
        ArrayList<String> parts = new ArrayList<>();
        int fromIndex = 1;
        while (fromIndex < path.length()) {
            int nextSep = path.indexOf('/', fromIndex);
            if (nextSep == -1) {
                nextSep = path.length();
            }
            parts.add(path.substring(fromIndex, nextSep));
            fromIndex = nextSep + 1;
        }
        if (path.endsWith("/")) {
            parts.add("");
        }
        return parts;
    }

    private static boolean isPartMatch(String patternPart, String pathPart) {
        return "*".equals(patternPart) || patternPart.startsWith(":") && pathPart.length() > 0 || patternPart.equals(pathPart);
    }

    public static boolean isPathMatch(String pattern, String path) {
        ArrayList<String> patternParts = splitPath(pattern);
        Iterator<String> patternIterator = patternParts.iterator();

        ArrayList<String> pathParts = splitPath(path);
        Iterator<String> pathIterator = pathParts.iterator();

        while (patternIterator.hasNext() && pathIterator.hasNext()) {
            String nextPattern = patternIterator.next();
            String nextPath = pathIterator.next();

            if (!isPartMatch(nextPattern, nextPath)) {
                return false;
            } else if ("*".equals(nextPattern) && !patternIterator.hasNext()) { // Last "*" in pattern
                return true;
            }
        }

        return !patternIterator.hasNext() && !pathIterator.hasNext();
    }

    /**
     * Call this method on the premise `path` matches `pattern`
     */
    public static Map<String, String> extractNamedParams(String pattern, String path) {
        Map<String, String> paramsHolder = new HashMap<>();

        ArrayList<String> patternParts = splitPath(pattern);
        Iterator<String> patternIterator = patternParts.iterator();

        ArrayList<String> pathParts = splitPath(path);
        Iterator<String> pathIterator = pathParts.iterator();

        while (patternIterator.hasNext() && pathIterator.hasNext()) {
            String nextPattern = patternIterator.next();
            String nextPath = pathIterator.next();
            if (nextPattern.startsWith(":")) {
                paramsHolder.put(nextPattern, nextPath);
            }
        }
        return Collections.unmodifiableMap(paramsHolder);
    }

    /**
     * Call this method on the premise `path` matches `pattern`
     */
    public static List<String> extractWildCards(String pattern, String path) {
        List<String> wildcardsHolder = new ArrayList<>();

        ArrayList<String> patternParts = splitPath(pattern);
        Iterator<String> patternIterator = patternParts.iterator();

        ArrayList<String> pathParts = splitPath(path);
        Iterator<String> pathIterator = pathParts.iterator();

        while (patternIterator.hasNext() && pathIterator.hasNext()) {
            String nextPattern = patternIterator.next();
            String nextPath = pathIterator.next();
            if ("*".equals(nextPattern)) {
                wildcardsHolder.add(nextPath);
            }
        }

        // Complement the matching parts of last "*" in pattern
        if (pathIterator.hasNext()) {
            int lastIdx = wildcardsHolder.size() - 1;
            String lastPart = wildcardsHolder.get(lastIdx);
            StringBuilder builder = new StringBuilder(lastPart);
            while (pathIterator.hasNext()) {
                builder.append('/').append(pathIterator.next());
            }
            wildcardsHolder.set(lastIdx, builder.toString());
        }
        return Collections.unmodifiableList(wildcardsHolder);
    }
}

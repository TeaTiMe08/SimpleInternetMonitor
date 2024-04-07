package de.teatime08.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class StackTracePrinter {
    /**
     * Creates a short representation with the most meaningful information of the stacktrace.
     * - Throws away the full classpath
     * - Prints class method line number of relevant trace.
     * - Gives you a one-liner exception to analyze
     * @param e the exception thrown to be analyzed.
     * @return a meaningful string representation of the stacktrace.
     */
    public static String stacktraceLineMessage(Throwable e) {
        final int maxNumberReturn = 2;
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage() + ": ");
        final String classpath = getFirstThreePathsUnderClasspath(StackTracePrinter.class.getName());
        List<StackTraceElement> foundElements = new LinkedList<>();
        for (StackTraceElement element : e.getStackTrace()) {
            if (getFirstThreePathsUnderClasspath(element.getClassName()).equals(classpath))
                foundElements.add(element);
        }
        foundElements.stream()
            .limit(maxNumberReturn)
            .forEach(el -> {
                String name = el.getClassName();
                if (name.contains("."))
                    name = name.substring(name.lastIndexOf(".") + 1);
                sb.append(name + "|" + el.getMethodName() + ":" + el.getLineNumber() + " ,");
            });
        return sb.toString();
    }

    private static String getFirstThreePathsUnderClasspath(String fullClassname) {
        return Arrays.stream(fullClassname.split(".")).limit(3).collect(Collectors.joining("."));
    }
}

package de.teatime08.netlatency.protocols;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class IRequestCheckerProvider {
    public static int maxTimeoutMs = 3000;
    static final Set<IRequestCheckerProvider> singletonRegister = new HashSet<>();
    static {
        ServiceLoader.load(IRequestCheckerProvider.class).forEach(singletonRegister::add);
        System.out.println("Added protocol ServiceProviders: " + Arrays.toString(singletonRegister.toArray()));
    }
    public abstract List<String> getSupportedProtocols();

    public abstract long measureLatencyInMs(String address) throws IOException;

    public static IRequestCheckerProvider getInstanceForAddress(final String address) {
        final RuntimeException unsupportedException = new UnsupportedOperationException("Protocol for \"" + address + "\" is not supported.");
        // collect all possible protocols
        Set<String> allProtocols = singletonRegister
            .stream()
            .flatMap(r -> r.getSupportedProtocols().stream())
            .collect(Collectors.toSet());
        // for comparing protocols based on the amount of starting letters they share with the address
        Comparator<String> firstLettersAgainstAddress = (s1, s2) -> {
            long count1 = numberOfStartingLetters(s1, address);
            long count2 = numberOfStartingLetters(s2, address);
            return (count1 != count2) ? Long.compare(count2, count1) : s1.compareTo(s2);
        };

        // sort the protocol list by the comparator and get the topmost element
        final String decidedOnProtocol = allProtocols
            .stream()
            .sorted(firstLettersAgainstAddress)
            .findFirst()
            .get();

        if ( ! address.startsWith(decidedOnProtocol))
            throw unsupportedException;
        return singletonRegister.stream()
            .filter(prod -> prod.getSupportedProtocols()
                .stream()
                .anyMatch( p -> p.equals(decidedOnProtocol)))
            .findFirst()
            .orElseThrow(() -> unsupportedException);
    }

    /**
     * Calculates the number of characters matching from the beginning of both strings.
     * @param s1 first string
     * @param s2 second string
     * @return zero if any is null; zero if the first character does not equal at both; one if the first char is equal but second is not; aso...
     */
    private static int numberOfStartingLetters(String s1, String s2) {
        if (s1 == null || s2 == null)
            return 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s2.length() - 1 < i)
                return i;
            if  (s2.charAt(i) != s1.charAt(i))
                return i;
        }
        return s1.length();
    }

    @Override
    public String toString() {
        return "{" + getClass().getName() + ", " + Arrays.toString(getSupportedProtocols().toArray()) + "}";
    }
}

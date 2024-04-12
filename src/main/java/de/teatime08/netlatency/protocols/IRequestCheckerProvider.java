package de.teatime08.netlatency.protocols;

import java.io.IOException;
import java.util.*;

public abstract class IRequestCheckerProvider {
    public static int maxTimeoutMs = 1000;
    static final Set<IRequestCheckerProvider> singletonRegister = new HashSet<>();
    static {
        ServiceLoader.load(IRequestCheckerProvider.class).forEach(singletonRegister::add);
        System.out.println("Added protocol ServiceProviders: " + Arrays.toString(singletonRegister.toArray()));
    }
    public abstract String[] getSupportedProtocols();

    public abstract long measureLatencyInMs(String address) throws IOException;

    public static IRequestCheckerProvider getInstanceForAddress(String address) {
        final String protocol = getProtocolFromAddress(address);
        return singletonRegister.stream()
            .filter( checker ->
                Arrays.asList(checker.getSupportedProtocols()).stream()
                    .anyMatch(supportedProt -> supportedProt.equalsIgnoreCase(protocol))
            )
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException("Protocol \"" + protocol + "\" is not supported."));
    }

    public static String getProtocolFromAddress(String address) {
        final String protocol;
        if (address.contains(":"))
            protocol = address.split(":")[0];
        else
            throw new UnsupportedOperationException("No protocol was provided in the address: " + address);
        return protocol;
    }

    @Override
    public String toString() {
        return "{" + getClass().getName() + ", " + Arrays.toString(getSupportedProtocols()) + "}";
    }
}

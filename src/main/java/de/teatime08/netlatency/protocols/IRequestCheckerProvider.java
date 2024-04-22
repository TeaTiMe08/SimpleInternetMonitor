package de.teatime08.netlatency.protocols;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class IRequestCheckerProvider {
    public static int maxTimeoutMs = 3000;
    static final Set<IRequestCheckerProvider> singletonRegister = new HashSet<>();
    static {
        ServiceLoader.load(IRequestCheckerProvider.class).forEach(singletonRegister::add);
        System.out.println("Added protocol ServiceProviders: " + Arrays.toString(singletonRegister.toArray()));
    }
    public abstract List<String> getSupportedProtocols();

    public abstract long measureLatencyInMs(String address) throws IOException;

    public static IRequestCheckerProvider getInstanceForAddress(String address) {
        for (int i = 0; i < address.length(); i++) {
            final String protocolMatch = address.substring(0, i + 1);
            List<IRequestCheckerProvider> stream = singletonRegister.stream()
                    .filter(checker ->
                            checker.getSupportedProtocols().stream()
                                    .anyMatch(prot -> prot.startsWith(protocolMatch)))
                    .collect(Collectors.toList());
            if (stream.size() > 1)
                continue;
            if (stream.isEmpty())
                break;
            return stream.get(0);
        }
        throw new UnsupportedOperationException("Protocol for \"" + address + "\" is not supported.");
    }

    @Override
    public String toString() {
        return "{" + getClass().getName() + ", " + Arrays.toString(getSupportedProtocols().toArray()) + "}";
    }
}

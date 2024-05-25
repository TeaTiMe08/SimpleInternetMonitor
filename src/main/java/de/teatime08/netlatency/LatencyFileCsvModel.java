package de.teatime08.netlatency;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum LatencyFileCsvModel {
    TIMESTAMP,
    WORKED,
    LATENCY_IN_MS,
    CALLED_ADDRESS;

    public static String toCSVHeader() {
        return Arrays.stream(values()).map(Enum::name).collect(Collectors.joining(","));
    }
}

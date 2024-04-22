package de.teatime08.netlatency.protocols.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class IPSocketProviderTest {
    private IPSocketProvider provider;


    @BeforeEach
    public void before() {
        provider = new IPSocketProvider();
    }

    @Test
    void measureLatencyInMs_withPort7RefusesConnection() throws IOException {
        Assertions.assertTrue(-1 != provider.measureLatencyInMs("80.69.96.12:53"));
    }

    @Test
    void measureLatencyInMs_knownProviders() throws IOException {
        Assertions.assertTrue(-1 != provider.measureLatencyInMs("1.1.1.1:53"));
        Assertions.assertTrue(-1 != provider.measureLatencyInMs("[2606:4700:4700::1111]:53"));
        Assertions.assertTrue(-1 != provider.measureLatencyInMs("8.8.8.8:53"));
    }
}
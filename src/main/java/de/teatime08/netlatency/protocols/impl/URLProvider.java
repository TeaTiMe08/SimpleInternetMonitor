package de.teatime08.netlatency.protocols.impl;

import de.teatime08.netlatency.protocols.IRequestCheckerProvider;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

public class URLProvider extends IRequestCheckerProvider {

    @Override
    public List<String> getSupportedProtocols() {
        return Arrays.asList("http", "jrt", "file", "jar");
    }
    /**
     * The actual measurement of the network latency.
     * Measures how long it takes to get a http connection up.
     * @return the millieseconds it takes to connect to a website.
     * @throws IOException is it is not possible to connecto to the website.
     */
    @Override
    public long measureLatencyInMs(String address) throws IOException {
        final URL urlConn = new URL(address);
        final URLConnection conn = urlConn.openConnection();
            conn.setConnectTimeout(maxTimeoutMs);
            long nanos = System.nanoTime();
        conn.connect();
        nanos = System.nanoTime() - nanos;
        conn.getInputStream().close();
        return (long) (nanos / 1_000_000d);
    }
}

package de.teatime08.netlatency.protocols.impl;

import de.teatime08.netlatency.protocols.IRequestCheckerProvider;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class IPSocketProvider extends IRequestCheckerProvider {
    @Override
    public String getProtocolDescriptor() {
        return "Socket";
    }

    /**
     * All protocols which start with a number assume it's
     * @return
     */
    @Override
    public List<String> getSupportedProtocols() {
        return Arrays.asList("[", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    }

    @Override
    public long measureLatencyInMs(String address) throws IOException {
        String ip = address.replace("[", "").replace("]", "").substring(0, address.lastIndexOf(":"));
        InetAddress inetAddress = InetAddress.getByName(ip);
        int port = Integer.parseInt(address.substring( 1 + address.lastIndexOf(":")));

        // Any Open port on other machine
        // openPort =  22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
        Socket soc = new Socket();
        long nanos = System.nanoTime();
        soc.connect(new InetSocketAddress(inetAddress, port), maxTimeoutMs);
        nanos = System.nanoTime() - nanos;
        return (long) (nanos / 1_000_000d);
    }
}

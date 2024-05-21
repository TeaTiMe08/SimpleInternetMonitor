package de.teatime08.netlatency.protocols.impl;

import de.teatime08.netlatency.protocols.IRequestCheckerProvider;
import de.teatime08.util.SSLTrustUtils;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class WssProvider extends IRequestCheckerProvider {
    @Override
    public String getProtocolDescriptor() {
        return "WSS";
    }

    @Override
    public List<String> getSupportedProtocols() {
        return Arrays.asList("ws", "wss");
    }

    @Override
    public long measureLatencyInMs(String address) throws IOException {

        final URI uri;
        try {
            uri = new URI(address);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        WebSocketClient mWs = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {}
            @Override
            public void onMessage(String s) {}
            @Override
            public void onClose(int i, String s, boolean b) {}
            @Override
            public void onError(Exception e) {}
        };
        mWs.setConnectionLostTimeout((int) (maxTimeoutMs / 1000.d));
        if (address.startsWith("wss"))
            mWs.setSocketFactory(SSLTrustUtils.totallyUnsafeSSLContext().getSocketFactory());
        try {
            long nanos = System.nanoTime();
            mWs.connectBlocking();
            nanos = System.nanoTime() - nanos;
            mWs.close();
            return (long) (nanos / 1_000_000d);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private class WebSocketImplMeasure extends WebSocketImpl {

        long nanosMeasured = 0;

        public WebSocketImplMeasure(WebSocketListener listener, List<Draft> drafts) {
            super(listener, drafts);
        }

        public WebSocketImplMeasure(WebSocketListener listener, Draft draft) {
            super(listener, draft);
        }

        @Override
        public void startHandshake(ClientHandshakeBuilder handshakedata) throws InvalidHandshakeException {
            long nanos = System.nanoTime();
            super.startHandshake(handshakedata);
            nanos = System.nanoTime() - nanos;
            nanosMeasured = (long) (nanos / 1_000_000d);
        }
    }
}
package de.teatime08.netlatency.protocols.impl;

import de.teatime08.netlatency.protocols.IRequestCheckerProvider;
import de.teatime08.util.SSLTrustUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpsProvider extends IRequestCheckerProvider {
    @Override
    public String getProtocolDescriptor() {
        return "HTTPS";
    }

    @Override
    public List<String> getSupportedProtocols() {
        return Arrays.asList("https");
    }

    @Override
    public long measureLatencyInMs(String address) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .sslSocketFactory(SSLTrustUtils.totallyUnsafeSSLContext().getSocketFactory(), new SSLTrustUtils.SSLTrustEverythingTrustManager())
            .hostnameVerifier((hostname, session) -> true)
            .connectTimeout(maxTimeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(maxTimeoutMs / 2, TimeUnit.MILLISECONDS)
            .readTimeout(maxTimeoutMs / 2, TimeUnit.MILLISECONDS)
            .build();
        Request request = new Request.Builder().url(address).build();
        Call call = client.newCall(request);
        long nanos = System.nanoTime();
        Response response = call.execute();
        nanos = System.nanoTime() - nanos;
        return (long) (nanos / 1_000_000d);
    }
}

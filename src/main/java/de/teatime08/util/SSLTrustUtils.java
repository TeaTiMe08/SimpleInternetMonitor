package de.teatime08.util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLTrustUtils {
    public static SSLContext totallyUnsafeSSLContext() {
        try {
            SSLContext unsafeSSLContext = SSLContext.getInstance("SSL");
            TrustManager[] unsafeManagers = new TrustManager[]{new SSLTrustEverythingTrustManager()};
            unsafeSSLContext.init(null, unsafeManagers, new java.security.SecureRandom());
            return unsafeSSLContext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class SSLTrustEverythingTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    }
}

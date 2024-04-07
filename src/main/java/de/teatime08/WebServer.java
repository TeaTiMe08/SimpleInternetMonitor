package de.teatime08;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.teatime08.config.StoredConfigLoader;
import de.teatime08.netlatency.NetLatencyFileInformation;
import de.teatime08.util.ResourceUtil;

import java.io.*;
import java.net.InetSocketAddress;

public class WebServer {
    private final NetLatencyFileInformation information;
    private final HttpServer server;

    public WebServer(NetLatencyFileInformation netLatencyFileInformation) throws IOException {
        this.information = netLatencyFileInformation;
        server = HttpServer.create(new InetSocketAddress("localhost", 8047), 0);
        server.createContext("/simpleInternetMonitor", HttpHandlerFactory.staticContent("/static/netspeed.html"));
        server.createContext("/favicon.ico", HttpHandlerFactory.staticContent("/static/favicon.ico"));
        server.createContext("/chart.js", HttpHandlerFactory.staticContent("/static/chart.js", "application/javascript"));
        server.createContext("/datafile", new DataFileHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public static class HttpHandlerFactory {
        public static HttpHandler staticContent(final String path) {
            return staticContent(path, null);
        }

        public static HttpHandler staticContent(final String path, final String mimeType) {
            HttpHandler httpHandler = new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    byte[] bytes = ResourceUtil.loadResource(path, this.getClass());
                    if (mimeType != null)
                        exchange.getResponseHeaders().add("Content-Type", mimeType);
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
            };
            return httpHandler;
        }
    }
    public class DataFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try (FileInputStream fileInputStream = new FileInputStream(information.getDataFileLocation())) {
                byte[] bytes = new byte[fileInputStream.available()];
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                dataInputStream.readFully(bytes);
                t.getResponseHeaders().add("Content-Disposition", "attachment; filename=" + new File(information.getDataFileLocation()).getName());
                t.getResponseHeaders().add("Content-Type", "text/csv");
                t.getResponseHeaders().add("Content-Length", "" + bytes.length);
                t.sendResponseHeaders(200, bytes.length);

                OutputStream os = t.getResponseBody();
                os.write(bytes);
                os.close();
            }
        }
    }
}

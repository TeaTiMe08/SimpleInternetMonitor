package de.teatime08.netlatency;

import de.teatime08.config.Config;
import de.teatime08.config.StoredConfigLoader;
import de.teatime08.util.StackTracePrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A class for continuous testing of the internet latency (dns + http).
 * Takes measures for connecting to a specific site (for comparison)
 * and appends the results to a csv file for later displaying.
 *
 * @author TeaTiMe08
 */
public class NetLatency implements NetLatencyFileInformation, Runnable, StoredConfigLoader.IUpdateConfiguration {
    private String netaddress;
    private int timeoutms = 1000;
    private String filename = "SimpleInternetMonitor.csv";
    private boolean shouldRun = true;
    String absoluteLogFile;
    private PrintWriter openCsvPrinter;
    private final StoredConfigLoader storedConfigLoader;

    /**
     * Creates a new instance of the NetLatency checker.
     * @throws IOException if the logging csv cannot be accessed.
     */
    public NetLatency(StoredConfigLoader storedConfigLoader) throws IOException {
        this.storedConfigLoader = storedConfigLoader;
        this.storedConfigLoader.subscribe(this);
        netaddress = storedConfigLoader.getConfig().selectedProviderDomain;
        absoluteLogFile = System.getProperty("user.home") + File.separator + filename;
        // check if file existed
        File f = new File(absoluteLogFile);
        final boolean created;
        if (!f.exists()) {
            f.createNewFile();
            created = true;
        } else
            created = false;

        openCsvPrinter = new PrintWriter(new FileWriter(f, true), true);

        if (created)
            openCsvPrinter.println(LatencyFileCsvModel.toCSVHeader());

        run();
    }

    /**
     * Starts the thread which continuously measures the internet latency.
     */
    @Override
    public void run() {
        Thread thread = new Thread(() -> {
            while (shouldRun) {
                try {
                    long fixedTimeoutMillies = 30_000L;
                    long nanos = System.nanoTime();
                    measureAndSave();
                    double timePassedMillies = (System.nanoTime() - nanos) / 1_000_000d;
                    timePassedMillies = Math.max(0, timePassedMillies);
                    Thread.sleep(fixedTimeoutMillies - (long)timePassedMillies);
                } catch (Throwable e) {
                    System.err.println(StackTracePrinter.stacktraceLineMessage(e));
                }
            }
        });
        thread.start();
    }

    /**
     * Takes a single measure and appends the result to the {@link #absoluteLogFile}.
     */
    public void measureAndSave() {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String stringTime = currentTimestamp.format(formatter);

        String[] csv = new String[LatencyFileCsvModel.values().length];
        csv[LatencyFileCsvModel.TIMESTAMP.ordinal()] = stringTime;
        IOException ex = null;
        try {
            csv[LatencyFileCsvModel.LATENCY_IN_MS.ordinal()] = "" + latencyCheckMillies();
            csv[LatencyFileCsvModel.WORKED.ordinal()] = "y";
        } catch (IOException e) {
            ex = e;
            csv[LatencyFileCsvModel.LATENCY_IN_MS.ordinal()] = "-1";
            csv[LatencyFileCsvModel.WORKED.ordinal()] = "n";
        }
        System.out.println(this.netaddress + " : " + csvToString(csv));
        openCsvPrinter.println(csvToString(csv));
        if (ex != null)
            System.err.println(StackTracePrinter.stacktraceLineMessage(ex));
    }

    private static String csvToString(String[] csv) {
        return Arrays.stream(csv).collect(Collectors.joining(","));
    }

    /**
     * The actual measurement of the network latency.
     * Measures how long it takes to get a http connection up.
     * @return the millieseconds it takes to connect to a website.
     * @throws IOException is it is not possible to connecto to the website.
     */
    private long latencyCheckMillies() throws IOException {
        final URL url = new URL(netaddress);
        final URLConnection conn = url.openConnection();
        conn.setConnectTimeout(timeoutms);
        long nanos = System.nanoTime();
        conn.connect();
        nanos = System.nanoTime() - nanos;
        conn.getInputStream().close();
        return (long) (nanos / 1_000_000d);
    }

    @Override
    public String getDataFileLocation() {
        return absoluteLogFile;
    }

    @Override
    public void configUpdated(Config config) {
        netaddress = config.selectedProviderDomain;
    }
}

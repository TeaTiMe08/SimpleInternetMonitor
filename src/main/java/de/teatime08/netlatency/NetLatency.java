package de.teatime08.netlatency;

import de.teatime08.config.Config;
import de.teatime08.config.StoredConfigLoader;
import de.teatime08.netlatency.protocols.IRequestCheckerProvider;
import de.teatime08.util.StackTracePrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private static final long scheduleForMeasurementInMillies = 60_000L; // measurement repeats every x millieseconds
    String absoluteLogFile;
    private PrintWriter openCsvPrinter;
    private final StoredConfigLoader storedConfigLoader;
    private IRequestCheckerProvider provider;

    /**
     * Creates a new instance of the NetLatency checker.
     * @throws IOException if the logging csv cannot be accessed.
     */
    public NetLatency(StoredConfigLoader storedConfigLoader) throws IOException {
        this.storedConfigLoader = storedConfigLoader;
        this.storedConfigLoader.subscribe(this);
        netaddress = storedConfigLoader.getConfig().selectedProviderDomain;
        absoluteLogFile = System.getProperty("user.home") + File.separator + filename;

        // create provider
        provider = IRequestCheckerProvider.getInstanceForAddress(netaddress);

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
                    long nanos = System.nanoTime();
                    if (isConnectedToLocalNetwork())
                        measureAndSave();
                    else
                        Thread.sleep(10);
                    double timePassedMillies = (System.nanoTime() - nanos) / 1_000_000d;
                    timePassedMillies = Math.max(0, timePassedMillies);
                    Thread.sleep(scheduleForMeasurementInMillies - (long)timePassedMillies);
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
        Throwable ex = null;
        try {
            csv[LatencyFileCsvModel.LATENCY_IN_MS.ordinal()] = "" + provider.measureLatencyInMs(netaddress);
            csv[LatencyFileCsvModel.WORKED.ordinal()] = "y";
        } catch (Throwable e) {
            ex = e;
            csv[LatencyFileCsvModel.LATENCY_IN_MS.ordinal()] = "-1";
            csv[LatencyFileCsvModel.WORKED.ordinal()] = "n";
        }
        System.out.println(this.netaddress + " : " + csvToString(csv));
        openCsvPrinter.println(csvToString(csv));
        if (ex != null)
            System.err.println(StackTracePrinter.stacktraceLineMessage(ex));
    }

    private boolean isConnectedToLocalNetwork() {
        List<NetworkInterface> interfaces = null;
        try {
            interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        } catch (SocketException e) {
            System.err.println("Could not retrieve NetworkInterfaces fom NIO. " + StackTracePrinter.stacktraceLineMessage(e));
            return false;
        }
        List<NetworkInterface> potentialConnects = new LinkedList<>();
        List<String> macs = new LinkedList<>();
        for (NetworkInterface inf : interfaces) {
            // filter out garbage, mostly for windows
            try {
                if (inf.isVirtual() || inf.getInterfaceAddresses().isEmpty() || Collections.list(inf.getInetAddresses()).isEmpty() || inf.isLoopback() || ! inf.isUp())
                    continue;
            } catch (SocketException e) {
                continue;
            }
            // filter out Hyper V for Windows, if it's enabled, those are always online
            if (inf.getDisplayName().contains("Hyper-V"))
                continue;
            // If windows is connected via Remote NDIS (something like USB-Tethering)
            if (inf.getDisplayName().contains("NDIS") && inf.getDisplayName().contains("MAC"))
                ;
            // Check if it's a real chip
            /*
            try {
                byte[] hardwareAddress = inf.getHardwareAddress();
                if (hardwareAddress == null)
                    continue;
                String hex = bytesToHex(hardwareAddress);
                if ( ! WiresharkManufModel.loadedMacs.contains(hex) && ! WiresharkManufModel.loadedMacs.contains(hex.substring(0,6)))
                    continue;
            } catch (SocketException e) {
                continue;
            }
             */
            potentialConnects.add(inf);
        }
        return ! potentialConnects.isEmpty();
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static String csvToString(String[] csv) {
        return Arrays.stream(csv).collect(Collectors.joining(","));
    }

    @Override
    public String getDataFileLocation() {
        return absoluteLogFile;
    }

    @Override
    public void configUpdated(Config config) {
        netaddress = config.selectedProviderDomain;
        provider = IRequestCheckerProvider.getInstanceForAddress(netaddress);
    }
}

package de.teatime08.netlatency;

import de.teatime08.util.ResourceUtil;
import de.teatime08.util.StackTracePrinter;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.HashSet;

/**
 * This model is a Fix-Width model for decoding wiresharks manuf list,
 * which is a list of all known MAC addresses for network chipsets.
 *
 * It is structured as a fix-with with tabs seperating the fields.
 *
 * @see <a href="https://www.wireshark.org/download/automated/data/manuf">https://www.wireshark.org/download/automated/data/manuf</a>
 * @author TeaTiMe08
 */
public enum WiresharkManufModel {
    MAC,
    WIRESHARK_ID,
    VENDOR_FULL_NAME;

    public static HashSet<String> loadedMacs = generateMACSetFromResources();

    public static HashSet<String> generateMACSetFromResources() {
        String[] lines = new String[0];
        try {
            lines = new String(ResourceUtil.loadResource("/manuf.txt", WiresharkManufModel.class)).split("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HashSet<String> macs = new HashSet<>();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("#"))
                continue;
            else
                macs.add(lines[i].split("\\t")[0].trim().replace(":", ""));
        }
        return macs;
    }

    public static boolean realChipInvolvedWithThisNI(NetworkInterface networkInterface) {
        try {
            byte[] hardwareAddress = networkInterface.getHardwareAddress();
            if (hardwareAddress == null)
                return false;
            String hex = bytesToHex(hardwareAddress);
            return WiresharkManufModel.loadedMacs.contains(hex) || WiresharkManufModel.loadedMacs.contains(hex.substring(0,6));
        } catch (IOException e) {
            System.err.println("Could not get hardware address for interface ex: " + StackTracePrinter.stacktraceLineMessage(e));
            return false;
        }
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

    public static String[] fromLine(String line) {
        String[] manuf = line.split("\\t");
        for (int i = 0; i < manuf.length; i++) {
            manuf[i] = manuf[i].trim();
        }
        return manuf;
    }
}

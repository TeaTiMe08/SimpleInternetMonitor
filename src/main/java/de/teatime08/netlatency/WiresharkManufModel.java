package de.teatime08.netlatency;

import de.teatime08.util.ResourceUtil;

import java.io.IOException;
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

    public static String[] fromLine(String line) {
        String[] manuf = line.split("\\t");
        for (int i = 0; i < manuf.length; i++) {
            manuf[i] = manuf[i].trim();
        }
        return manuf;
    }
}

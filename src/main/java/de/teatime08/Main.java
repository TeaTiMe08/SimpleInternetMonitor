package de.teatime08;

import de.teatime08.config.StoredConfigLoader;
import de.teatime08.gui.AwtSystemTray;
import de.teatime08.gui.LookAndFeel;
import de.teatime08.netlatency.NetLatency;

import java.io.IOException;

/**
 * Main class to call to start the GUI, the webserver and the internet measurement thread.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        LookAndFeel lookAndFeel = new LookAndFeel();
        StoredConfigLoader storedConfigLoader = new StoredConfigLoader();
        NetLatency netLatency = new NetLatency(storedConfigLoader);

        WebServer webServer = new WebServer(netLatency);

        AwtSystemTray tray = new AwtSystemTray(storedConfigLoader);
        tray.createSystemTray();
    }
}
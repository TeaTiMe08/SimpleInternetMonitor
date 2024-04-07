package de.teatime08.gui;

import de.teatime08.config.StoredConfigLoader;
import de.teatime08.util.ResourceUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class AwtSystemTray {

    private StoredConfigLoader storedConfigLoader;

    public AwtSystemTray(StoredConfigLoader storedConfigLoader) {
        this.storedConfigLoader = storedConfigLoader;
    }

    ActionListener actionOpenWebsite = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                URI statusMonitorWebpage = new URI("http://localhost:8047/simpleInternetMonitor");
                Desktop.getDesktop().browse(statusMonitorWebpage);
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    };

    public void createSystemTray() {
        TrayIcon trayIcon = null;
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            SystemTray tray = SystemTray.getSystemTray();

            // load an image
            Image image;
            try {
                InputStream imageStream = new ByteArrayInputStream(ResourceUtil.loadResource("/icon.png", this.getClass()));
                image = ImageIO.read(imageStream);
                image = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // create a popup menu
            PopupMenu popup = new PopupMenu();

            // create menu item for the default action
            MenuItem quitItem = new MenuItem("Quit");
            quitItem.addActionListener(e -> {
                System.exit(0);
            });
            popup.add(quitItem);

            // create another opten item
            MenuItem openItem = new MenuItem("Open");
            openItem.addActionListener(actionOpenWebsite);
            popup.add(openItem);

            MenuItem providerItem = new MenuItem("Select Provider");
            providerItem.addActionListener(new SelectProviderActionFrame(storedConfigLoader));
            popup.add(providerItem);

            // construct a TrayIcon
            trayIcon = new TrayIcon(image, "Simple Internet Monitor", popup);
            // set the TrayIcon properties
            trayIcon.addActionListener(actionOpenWebsite);
            trayIcon.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    actionOpenWebsite.actionPerformed(null);
                }
                @Override
                public void mousePressed(MouseEvent e) {}
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {}
            });
            // ...
            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
            // ...
        } else {
            // disable tray option in your application or
            // perform other actions
        }
    }
}

package de.teatime08.gui;

import de.teatime08.config.StoredConfigLoader;
import de.teatime08.util.ResourceUtil;
import dorkbox.systemTray.SystemTray;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        SystemTray systemTray = SystemTray.get("SimpleInternetMonitor");
        if (systemTray == null) {
            throw new RuntimeException("Unable to load SystemTray!");
        }

        systemTray.setTooltip("Measures Internet availability.");

        // load an image
        Image image;
        try {
            InputStream imageStream = new ByteArrayInputStream(ResourceUtil.loadResource("/static/icon.png", this.getClass()));
            image = ImageIO.read(imageStream);
            image = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            systemTray.setImage(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // opens the website
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(actionOpenWebsite);

        // create provider menu
        JMenuItem providerItem = new JMenuItem("Select Provider");
        SelectProviderActionDialog selectProviderActionDialog = new SelectProviderActionDialog(storedConfigLoader);
        providerItem.addActionListener(e -> {
            selectProviderActionDialog.setVisible(true);
            selectProviderActionDialog.setLocationRelativeTo(null);
        });

        // create menu item for the default action
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(e -> System.exit(0));

        systemTray.getMenu().add(openItem);
        systemTray.getMenu().add(providerItem);
        systemTray.getMenu().add(quitItem);
    }
}

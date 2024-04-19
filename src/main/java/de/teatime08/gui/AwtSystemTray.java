package de.teatime08.gui;

import de.teatime08.config.StoredConfigLoader;
import de.teatime08.util.ResourceUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
            final JPopupMenu popup = new JPopupMenu();

            // create menu item for the default action
            JMenuItem quitItem = new JMenuItem("Quit");
            quitItem.addActionListener(e -> {
                System.exit(0);
                popup.setVisible(false);
            });

            // create another opten item
            JMenuItem openItem = new JMenuItem("Open");
            openItem.addActionListener(actionOpenWebsite);
            openItem.addActionListener(close -> {
                popup.setVisible(false);
            });

            JMenuItem providerItem = new JMenuItem("Select Provider");
            SelectProviderActionFrame selectProviderActionFrame = new SelectProviderActionFrame(storedConfigLoader);
            providerItem.addActionListener(e -> selectProviderActionFrame.setVisible(true));
            providerItem.addActionListener(list -> popup.setVisible(false));

            // add all options to the menu in order.
            popup.add(openItem);
            popup.add(providerItem);
            popup.add(quitItem);

            // construct a TrayIcon
            trayIcon = new TrayIcon(image, "Simple Internet Monitor");
            // set the TrayIcon properties
            trayIcon.addActionListener(actionOpenWebsite);
            trayIcon.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        actionOpenWebsite.actionPerformed(null);
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        Point mouse = MouseInfo.getPointerInfo().getLocation();
                        popup.show(null, mouse.x,mouse.y);
                        Thread autoclose = new Thread(() -> {
                            try {
                                Thread.sleep(7000);
                                popup.setVisible(false);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        autoclose.start();
                    }
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
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
        } else {
            // disable tray option in your application or
            // perform other actions
        }
    }
}

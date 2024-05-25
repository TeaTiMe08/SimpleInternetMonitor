package de.teatime08.gui;

import de.teatime08.config.StoredConfigLoader;
import de.teatime08.netlatency.protocols.IRequestCheckerProvider;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AddNewProviderDialog extends JDialog {
    private final StoredConfigLoader storedConfigLoader;
    private final ITableReload iTableReload;

    private final JPanel formPanel;
    private final JTextField nameField, addressField;
    private final JButton submitButton, testButton;

    public AddNewProviderDialog(StoredConfigLoader storedConfigLoader, ITableReload iTableReload) {
        this.storedConfigLoader = storedConfigLoader;
        this.iTableReload = iTableReload;
        setTitle("Simple Internet Monitor: Add your own provider");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setAlwaysOnTop(true);
        setVisible(false);
        setLocationRelativeTo(null);
        setSize(300, 140);

        formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(2, 2));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);
        formPanel.add(new JLabel("Provider address:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        add(Box.createHorizontalStrut(10), BorderLayout.EAST);
        add(formPanel, BorderLayout.CENTER);
        add(Box.createHorizontalStrut(10), BorderLayout.WEST);

        submitButton = new JButton("Submit");
        testButton = new JButton("Test");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(testButton, BorderLayout.SOUTH);
        buttonPanel.add(submitButton, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.SOUTH);

        testButton.addActionListener(ev -> {
            if ( ! addressField.getText().isEmpty()) {
                String domainOrIpAddress = addressField.getText();
                try {
                    IRequestCheckerProvider provider = IRequestCheckerProvider.getInstanceForAddress(domainOrIpAddress);
                    try {
                        long howLong = provider.measureLatencyInMs(domainOrIpAddress);
                        JOptionPane.showMessageDialog(null, "Connected. This took " + howLong + "ms");
                    } catch (Throwable e) {
                        JOptionPane.showMessageDialog(null, "Could not connect to address; " + e.getMessage());
                    }
                } catch (UnsupportedOperationException e) {
                    JOptionPane.showMessageDialog(null, "The address format provided is not supported.\n" + domainOrIpAddress);
                }
                IRequestCheckerProvider.getInstanceForAddress(domainOrIpAddress);
            } else
                JOptionPane.showMessageDialog(null, "Please enter a provider first.");
        });

        submitButton.addActionListener(ev -> {
            storedConfigLoader.getConfig().customProviders.put(nameField.getText(), addressField.getText());
            try {
                storedConfigLoader.store();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            iTableReload.reloadTableContentSelect(nameField.getText());
            // clean the input fields
            nameField.setText("");
            addressField.setText("");
            setVisible(false);
        });
    }
}

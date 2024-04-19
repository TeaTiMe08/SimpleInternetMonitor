package de.teatime08.gui;

import de.teatime08.config.AvailibleProviders;
import de.teatime08.config.StoredConfigLoader;
import de.teatime08.util.StackTracePrinter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SelectProviderActionFrame extends JDialog {
    private JButton selectButton;
    private JTable itemTable;

    private final SelectProviderActionFrame thus = this;

    private final StoredConfigLoader storedConfigLoader;

    public SelectProviderActionFrame(StoredConfigLoader storedConfigLoader) {
        this.storedConfigLoader = storedConfigLoader;

        setTitle("Item Selection");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(400, 300);

        // load providers
        AvailibleProviders availibleProviders;
        try {
            availibleProviders = new AvailibleProviders();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        String[][] items = new String[availibleProviders.size()][AvailibleProviders.ProviderCSVModel.values().length];
        AvailibleProviders.Provider[] providers = availibleProviders.toArray(new AvailibleProviders.Provider[availibleProviders.size()]);
        for (int i = 0; i < items.length; i++) {
            items[i] = new String[]{providers[i].domainOrIp, providers[i].providerName, providers[i].country};
        }

        // Create table model
        DefaultTableModel tableModel = new DefaultTableModel(items, new String[]{"DOMAIN_OR_IP", "PROVIDER_NAME", "COUNTRY"});

        // Create item table
        itemTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create buttons
        selectButton = new JButton("Select");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Select button action
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = itemTable.getSelectedRow();
                if (selectedRow != -1) {
                    String domainOrIpAddress = (String) itemTable.getValueAt(selectedRow, 0);
                    storedConfigLoader.getConfig().selectedProviderDomain = domainOrIpAddress;
                    try {
                        storedConfigLoader.store();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "This did not work: Exception @ " + StackTracePrinter.stacktraceLineMessage(ex));
                        throw new RuntimeException(ex);
                    }
                    thus.dispose();
                    thus.setVisible(false);
                    JOptionPane.showMessageDialog(null, "Switched to " + domainOrIpAddress);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a Provider first.");
                }
            }
        });
        setLocationRelativeTo(null);
    }
}

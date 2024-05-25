package de.teatime08.gui;

import de.teatime08.config.AvailibleProviders;
import de.teatime08.config.Config;
import de.teatime08.config.StoredConfigLoader;
import de.teatime08.netlatency.protocols.IRequestCheckerProvider;
import de.teatime08.util.StackTracePrinter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.regex.PatternSyntaxException;

public class SelectProviderActionDialog extends JDialog implements ITableReload {
    private final SelectProviderActionDialog thus = this; // stupid swing sometimes needs th...
    private final StoredConfigLoader storedConfigLoader;
    private static final String[] tableColumns = new String[]{"DOMAIN_OR_IP", "PROVIDER_NAME", "COUNTRY", "PROTOCOL"};

    private JButton selectButton, testButton, addButton;
    private JTable itemTable;
    private AddNewProviderDialog addNewProviderDialog;

    public SelectProviderActionDialog(StoredConfigLoader storedConfigLoader) {
        this.storedConfigLoader = storedConfigLoader;
        this.addNewProviderDialog = new AddNewProviderDialog(storedConfigLoader, this);

        setTitle("Simple Internet Monitor: Select your measuring provider");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(800, 600);

        // load providers
        String[][] items = loadProvidersForTableItems();

        // Create table model
        DefaultTableModel tableModel = new DefaultTableModel(items, tableColumns);

        // Create item table
        itemTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        // create search filter
        JTextField searchTextField = new JTextField();
        searchTextField.setPreferredSize(new Dimension(150, 23));
        SelectProviderActionDialog.AddFilter(itemTable, searchTextField);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search (regex):"));
        searchPanel.add(searchTextField);

        add(searchPanel, BorderLayout.NORTH);

        // Create buttons
        addButton = new JButton("+");
        testButton = new JButton("Test");
        selectButton = new JButton("Select");

        // add buttons to lower panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(testButton);
        buttonPanel.add(selectButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(ev -> addNewProviderDialog.setVisible(true));
        // Test button action
        testButton.addActionListener(ev -> {
            int selectedRow = itemTable.getSelectedRow();
            if (selectedRow != -1) {
                String domainOrIpAddress = (String) itemTable.getValueAt(selectedRow, 0);
                try {
                    IRequestCheckerProvider provider = IRequestCheckerProvider.getInstanceForAddress(domainOrIpAddress);
                    try {
                        long howLong = provider.measureLatencyInMs(domainOrIpAddress);
                        JOptionPane.showMessageDialog(null, "Connected. This took " + howLong + "ms");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Could not connect to address; " + e.getMessage());
                    }
                } catch (UnsupportedOperationException e) {
                    JOptionPane.showMessageDialog(null, "The address format provided is not supported.\n" + domainOrIpAddress);
                }
                IRequestCheckerProvider.getInstanceForAddress(domainOrIpAddress);
            } else
                JOptionPane.showMessageDialog(null, "Please select a Provider first.");
        });

        // Select button action
        selectButton.addActionListener(ev -> {
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
        });
    }

    private String[][] loadProvidersForTableItems() {
        AvailibleProviders availibleProviders;
        try {
            availibleProviders = new AvailibleProviders();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        Config config = storedConfigLoader.getConfig();
        int finalLength = availibleProviders.size() + config.customProviders.size();

        String[][] items = new String[finalLength][AvailibleProviders.ProviderCSVModel.values().length + 1];

        // add custom providers first
        int index = 0;
        for (String key : config.customProviders.keySet()) {
            String addr = config.customProviders.get(key);
            String protocol = "";
            try {
                protocol = IRequestCheckerProvider.getInstanceForAddress(addr).getProtocolDescriptor();
            } catch (UnsupportedOperationException e) {}
            items[index] = new String[]{addr, key, "USER", protocol};
            index++;
        }

        // add system entries
        AvailibleProviders.Provider[] providers = availibleProviders.stream()
            .sorted(Comparator.comparing(o -> o.country))
            .sorted((o1, o2) -> "World".equals(o1.country) ? -1 : 1)
            .toArray(AvailibleProviders.Provider[]::new);
        for (int i = 0; i < providers.length; i++) {
            String protocol = "";
            try {
                protocol = IRequestCheckerProvider.getInstanceForAddress(providers[i].domainOrIp).getProtocolDescriptor();
            } catch (UnsupportedOperationException e) {}
            items[i + index] = new String[]{providers[i].domainOrIp, providers[i].providerName, providers[i].country, protocol};
        }
        return items;
    }

    public void reloadTableContentSelect(String name) {
        itemTable.setModel(new DefaultTableModel(loadProvidersForTableItems(), tableColumns));
        TableModel model = itemTable.getModel();
        for (int count = 0; count < model.getRowCount(); count++){
            // column index is 1 because second column is the name of the provider
            if (name.equals(model.getValueAt(count, 1).toString())) {
                itemTable.setRowSelectionInterval(count, count);
            }

        }
    }

    public static void AddFilter(JTable tbl, JTextField txtSearch){ //, Integer SearchColumnIndex) {

        DefaultTableModel model = (DefaultTableModel) tbl.getModel();

        final TableRowSorter< DefaultTableModel> sorter = new TableRowSorter< DefaultTableModel>(model);
        tbl.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                OnChange();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                OnChange();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                OnChange();
            }
            public void OnChange() {
                String txt = txtSearch.getText().toLowerCase();
                if (txt.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    try {
                        final int rowsCount = tbl.getModel().getRowCount();
                        int[] rowIndices = new int[rowsCount];
                        for (int i = 0; i < rowIndices.length; i++)
                            rowIndices[i] = i;
                        sorter.setRowFilter(RowFilter.regexFilter("^(?i).*" + txt, rowIndices));
                    } catch (PatternSyntaxException pse) {
                        System.out.println("Bad regex pattern");
                    }
                }

            }
        });
    }

}

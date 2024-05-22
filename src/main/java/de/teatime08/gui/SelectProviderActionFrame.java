package de.teatime08.gui;

import de.teatime08.config.AvailibleProviders;
import de.teatime08.config.StoredConfigLoader;
import de.teatime08.netlatency.protocols.IRequestCheckerProvider;
import de.teatime08.util.StackTracePrinter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.regex.PatternSyntaxException;

public class SelectProviderActionFrame extends JDialog {
    private final SelectProviderActionFrame thus = this; // stupid swing sometimes needs th...
    private final StoredConfigLoader storedConfigLoader;

    private JButton selectButton;
    private JTable itemTable;

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
        String[][] items = new String[availibleProviders.size()][AvailibleProviders.ProviderCSVModel.values().length + 1];
        AvailibleProviders.Provider[] providers = availibleProviders.toArray(new AvailibleProviders.Provider[availibleProviders.size()]);
        for (int i = 0; i < items.length; i++) {
            String protocol = "";
            try {
                protocol = IRequestCheckerProvider.getInstanceForAddress(providers[i].domainOrIp).getProtocolDescriptor();
            } catch (UnsupportedOperationException e) {}
            items[i] = new String[]{providers[i].domainOrIp, providers[i].providerName, providers[i].country, protocol};
        }

        // Create table model
        DefaultTableModel tableModel = new DefaultTableModel(items, new String[]{"DOMAIN_OR_IP", "PROVIDER_NAME", "COUNTRY", "PROTOCOL"});

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
        SelectProviderActionFrame.AddFilter(itemTable, searchTextField);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search (regex):"));
        searchPanel.add(searchTextField);

        add(searchPanel, BorderLayout.NORTH);

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

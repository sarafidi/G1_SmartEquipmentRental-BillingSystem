package view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controller.RentalController;
import model.rental.Rental;
import util.SessionManager;
import util.Validator;

public class RentalPanel extends JPanel {
    private final RentalController controller = new RentalController();
    
    // UI Components untuk Checkout Form
    private JTextField txtUserId;
    private JTextField txtEquipmentId;
    private JTextField daysField;
    private JButton btnRent;

    // UI Components untuk Return Form
    private JTextField txtRentalId;
    private JComboBox<String> comboCondition;
    private JButton btnReturn;

    // UI Components untuk Jadual
    private JTable rentalTable;
    private DefaultTableModel tableModel;

    public RentalPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ==========================================
        // 1. BAHAGIAN ATAS: BORANG (FORM PANEL)
        // ==========================================
        JPanel formsContainer = new JPanel(new GridLayout(1, 2, 20, 0));

        // ---- BORANG CHECKOUT ----
        JPanel checkoutPanel = new JPanel(new GridBagLayout());
        checkoutPanel.setBorder(BorderFactory.createTitledBorder("Equipment Checkout (Rent)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        checkoutPanel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        txtUserId = new JTextField(10);
        checkoutPanel.add(txtUserId, gbc);

        // Autofill current user id kalau ada guna rule #5 Sara
        try {
            if (SessionManager.getInstance().getCurrentUser() != null) {
                txtUserId.setText(SessionManager.getInstance().getCurrentUser().getUserId());
            }
        } catch (Exception e) {
            // Pasrah kalau SessionManager belum init
        }

        gbc.gridx = 0; gbc.gridy = 1;
        checkoutPanel.add(new JLabel("Equipment ID:"), gbc);
        gbc.gridx = 1;
        txtEquipmentId = new JTextField(10);
        checkoutPanel.add(txtEquipmentId, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        checkoutPanel.add(new JLabel("Duration (Days):"), gbc);
        gbc.gridx = 1;
        daysField = new JTextField(10);
        checkoutPanel.add(daysField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        btnRent = new JButton("Checkout Equipment");
        btnRent.addActionListener(e -> onRentClick());
        checkoutPanel.add(btnRent, gbc);

        // ---- BORANG RETURN ----
        JPanel returnPanel = new JPanel(new GridBagLayout());
        returnPanel.setBorder(BorderFactory.createTitledBorder("Equipment Return"));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        gbc2.gridx = 0; gbc2.gridy = 0;
        returnPanel.add(new JLabel("Rental ID:"), gbc2);
        gbc2.gridx = 1;
        txtRentalId = new JTextField(10);
        returnPanel.add(txtRentalId, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 1;
        returnPanel.add(new JLabel("Condition:"), gbc2);
        gbc2.gridx = 1;
        comboCondition = new JComboBox<>(new String[]{"Excellent", "Good", "Damaged"});
        returnPanel.add(comboCondition, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 2; gbc2.gridwidth = 2;
        btnReturn = new JButton("Return Equipment");
        btnReturn.addActionListener(e -> onReturnClick());
        returnPanel.add(btnReturn, gbc2);

        // Masukkan dua borang dalam satu baris
        formsContainer.add(checkoutPanel);
        formsContainer.add(returnPanel);
        add(formsContainer, BorderLayout.NORTH);

        // ==========================================
        // 2. BAHAGIAN TENGAH: JADUAL SEWAAN ACTIVE
        // ==========================================
        String[] columns = {"Rental ID", "User ID", "Equipment ID", "Days Rented", "Overdue Status"};
        tableModel = new DefaultTableModel(columns, 0);
        rentalTable = new JTable(tableModel);
        
        // Listener bila klik row kat table, auto fill Rental ID kat form return
        rentalTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = rentalTable.getSelectedRow();
            if (selectedRow != -1) {
                txtRentalId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(rentalTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Active Rentals History & Status"));
        add(scrollPane, BorderLayout.CENTER);

        // Muat data awal-awal masa panel dibuka
        refreshTable();
    }

    public void onRentClick() {
        String userId = txtUserId.getText().trim();
        String equipmentId = txtEquipmentId.getText().trim();
        String daysStr = daysField.getText().trim();

        // Validate basic input menggunakan Validator
        if (!Validator.isNonEmpty(userId) || !Validator.isNonEmpty(equipmentId) || !Validator.isNonEmpty(daysStr)) {
            JOptionPane.showMessageDialog(this, "Please fill in all checkout fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int days = Integer.parseInt(daysStr);
            if (!Validator.isValidDays(days)) {
                JOptionPane.showMessageDialog(this, "Rental days must be greater than 0!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Panggil Controller untuk proses sewaan
            Rental rental = controller.rentEquipment(userId, equipmentId, days);
            if (rental != null) {
                JOptionPane.showMessageDialog(this, "Rental processing successful! ID: " + rental.getRentalId(), "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
                
                // Clear borang checkout
                txtEquipmentId.setText("");
                daysField.setText("");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Duration days must be a numeric integer value!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Checkout Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onReturnClick() {
        String rentalId = txtRentalId.getText().trim();
        String condition = (String) comboCondition.getSelectedItem();

        if (!Validator.isNonEmpty(rentalId)) {
            JOptionPane.showMessageDialog(this, "Please select or input a valid Rental ID to return!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Panggil Controller untuk proses pemulangan
            controller.returnEquipment(rentalId, condition);
            JOptionPane.showMessageDialog(this, "Equipment returned successfully! Invoice/Bill automatically updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
            
            // Clear borang return
            txtRentalId.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Return Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshTable() {
        // Reset/kosongkan table dulu
        tableModel.setRowCount(0);

        try {
            // Tarik data paling suci dari controller
            List<Rental> activeRentals = controller.listAllRentals();
            for (Rental rental : activeRentals) {
                Object[] rowData = {
                    rental.getRentalId(),
                    rental.getUser().getUserId(),
                    rental.getEquipment().getEquipmentId(),
                    rental.getDaysRented(),
                    rental.isOverdue() ? "🔴 OVERDUE" : "ON TIME"
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            // Fail silent or show error if database kosong
        }
    }
}
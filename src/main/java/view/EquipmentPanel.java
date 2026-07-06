package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import controller.EquipmentController;
import model.UserType;
import model.equipment.ElectronicsEquipment;
import model.equipment.Equipment;
import model.equipment.LabEquipment;
import model.equipment.MediaEquipment;
import util.IDGenerator;
import util.SessionManager;

public class EquipmentPanel extends JPanel {
    private final EquipmentController controller = new EquipmentController();

    // == Table =====================================================
    private JTable equipmentTable;
    private DefaultTableModel tableModel;

    // == Search bar ================================================
    private JTextField searchField;
    JComboBox<String> filterBox;

    // == Add form -> shared fields =================================
    private JComboBox<String> typeBox;
    private JTextField nameField, rateField;

    // == Add form -> card layout for specific fields ===============
    private JPanel extraPanel;
    private CardLayout cardLayout;

    // Electronics card
    private JTextField warrantyField;

    // Media card
    private JCheckBox depositCheckbox;
    private JTextField depositAmountField;

    // Lab card
    private JTextField hazardLevelField;
    private JCheckBox certCheckbox;

    // == Constructor ===============================================
    public EquipmentPanel() {
        setLayout(new BorderLayout(0, 5));
        buildTopBar();
        buildTable();
        buildAddForm();     // only ADMIN can see
        refreshTable();
    }

    // == NORTH - Search bar ================================================
    private void buildTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(20);
        String[] items = {"All", "Electronics", "Media", "Lab"};
        filterBox = new JComboBox<>(items);
        JButton searchBtn = new JButton("Search");

        topBar.add(new JLabel("Search: "));
        topBar.add(searchField);
        topBar.add(new JLabel("Category: "));
        topBar.add(filterBox);
        topBar.add(searchBtn);

        add(topBar, BorderLayout.NORTH);
    }

    // == CENTER - Equipment table ================================================
    private void buildTable() {
        String[] columns = {"ID", "Name", "Category", "Daily Rate (RM)", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; };
        };
        equipmentTable = new JTable(tableModel);
        equipmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(equipmentTable), BorderLayout.CENTER);
    }

    // == SOUTH - Add / Remove form (ADMIN only) ==================================
    private void buildAddForm() {
        // hide entire form if not admin
        boolean isAdmin = SessionManager.getInstance().getCurrentUser().getUserType() == UserType.ADMIN;
        if (!isAdmin) return;

        JPanel south = new JPanel(new BorderLayout());


        // == Row 1: shared fields + type dropdown ================================
        JPanel sharedRow = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String[] equipType = {"Electronics", "Media", "Lab"};
        typeBox = new JComboBox<>(equipType);
        nameField = new JTextField(14);
        rateField = new JTextField(7);

        // swap extra-field card when type changes
        typeBox.addActionListener(e
                -> cardLayout.show(extraPanel, (String) typeBox.getSelectedItem())
        );

        sharedRow.add(new JLabel("Type: "));
        sharedRow.add(typeBox);
        sharedRow.add(new JLabel("Name: "));
        sharedRow.add(nameField);
        sharedRow.add(new JLabel("Rate (RM): "));
        sharedRow.add(rateField);

        // == Row 2: dynamic extra fields by CardLayout ===========================
        cardLayout = new CardLayout();
        extraPanel = new JPanel(cardLayout);

        // Electronics card
        JPanel electronicsCard = new JPanel(new FlowLayout(FlowLayout.LEFT));
        warrantyField = new JTextField(5);
        electronicsCard.add(new JLabel("Warranty (months): "));
        electronicsCard.add(warrantyField);

        // Media card
        JPanel mediaCard = new JPanel(new FlowLayout(FlowLayout.LEFT));
        depositCheckbox = new JCheckBox("Requires Deposit");
        depositAmountField = new JTextField(7);
        depositAmountField.setEnabled(false);   // disabled until checkbox ticked
        depositCheckbox.addActionListener(e
                -> depositAmountField.setEnabled(depositCheckbox.isSelected())
        );
        mediaCard.add(depositCheckbox);
        mediaCard.add(new JLabel("Deposit Amount (RM): "));
        mediaCard.add(depositAmountField);

        // Lab card
        JPanel labCard = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hazardLevelField = new JTextField(3);
        certCheckbox = new JCheckBox("Requires Certificate");
        labCard.add(new JLabel("Hazard Level (1-5): "));
        labCard.add(hazardLevelField);
        labCard.add(certCheckbox);

        // register all 3 cards - key must match typeBox values exactly
        extraPanel.add(electronicsCard, "Electronics");
        extraPanel.add(mediaCard, "Media");
        extraPanel.add(labCard, "Lab");

        // == Row 3: dynamic extra fields by CardLayout ===========================
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addbtn = new JButton("Add Equipment");
        JButton removebtn = new JButton("Remove Selected");
        addbtn.addActionListener(e -> onAddClick());
        removebtn.addActionListener(e -> onRemoveClick());
        btnRow.add(addbtn);
        btnRow.add(removebtn);

        // == Assemble south panel ================================================
        JPanel formRows = new JPanel(new GridLayout(3, 1));
        formRows.add(sharedRow);
        formRows.add(extraPanel);
        formRows.add(btnRow);

        south.setBorder(BorderFactory.createTitledBorder("Add New Equipment"));
        south.add(formRows, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    // == Actions ================================================================
    public void refreshTable() {
        tableModel.setRowCount(0);   // clear existing rows first

        for (Equipment e : controller.listAllEquipment()) {
            tableModel.addRow(new Object[]{
                    e.getEquipmentId(),
                    e.getName(),
                    e.getCategory(),
                    String.format("%.2f", e.getDailyRate()),
                    e.isAvailable() ? "Available" : "Not Available"
            });
        }
    }

    public void onSearchClick() {
        String keyword = searchField.getText().trim().toLowerCase();
        String category = (String) filterBox.getSelectedItem();

        tableModel.setRowCount(0);

        for (Equipment e : controller.listAllEquipment()) {
            boolean matchName = e.getName().toLowerCase().contains(keyword)
                    || e.getEquipmentId().toLowerCase().contains(keyword);

            boolean matchCategory =  category.equalsIgnoreCase("All")
                    || e.getCategory().equals(category);

            if (matchName && matchCategory) {
                tableModel.addRow(new Object[]{
                        e.getEquipmentId(),
                        e.getName(),
                        e.getCategory(),
                        String.format("%.2f", e.getDailyRate()),
                        e.isAvailable() ? "Available" : "Not Available"
                });
            }
        }
    }

    private void onAddClick() {
        try {
            String id = IDGenerator.generateEquipmentId();
            String name = nameField.getText().trim();
            double rate = Double.parseDouble(rateField.getText().trim());
            String type = (String) typeBox.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.");
                return;
            }

            Equipment eq = switch (type) {
                case "Electronics" -> new ElectronicsEquipment(
                        id, name, rate,
                        Integer.parseInt(warrantyField.getText().trim())
                );
                case "Media" -> new MediaEquipment(
                        id, name, rate,
                        depositCheckbox.isSelected(),
                        depositCheckbox.isSelected()
                                ? Double.parseDouble(depositAmountField.getText().trim())
                                : 0.0
                );
                case "Lab" -> new LabEquipment(
                        id, name, rate,
                        Integer.parseInt(hazardLevelField.getText().trim()),
                        certCheckbox.isSelected()
                );
                default -> throw new IllegalArgumentException("Unknown type");
            };

            controller.addEquipment(eq);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Equipment added! ID: " + id);

            // clear fields after succesfull add
            nameField.setText("");
            rateField.setText("");
            warrantyField.setText("");
            hazardLevelField.setText("");
            depositAmountField.setText("");
            depositCheckbox.setSelected(false);
            certCheckbox.setSelected(false);
            depositAmountField.setEnabled(false);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers for Rate / Warranty / Hazard Level.",
                    "Invalid input",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onRemoveClick() {
        int selectedRow = equipmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to remove.");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove " + name + "(" + id + ")?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            controller.removeEquipment(id);
            refreshTable();
        }
    }

}

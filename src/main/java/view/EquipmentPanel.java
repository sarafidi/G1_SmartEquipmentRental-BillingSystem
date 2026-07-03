package view;

import controller.EquipmentController;

import javax.swing.*;
import java.awt.*;

public class EquipmentPanel extends JPanel {
    private final EquipmentController controller = new EquipmentController();
    private JTable equipmentTable;
    private JTextField searchField;

    public EquipmentPanel() {
        setLayout(new BorderLayout());
        buildUI();
        refreshTable();
    }

    private void buildUI() {
        // TODO LEADER: build full equipment search, browse, and admin add/edit form
        equipmentTable = new JTable();
        searchField = new JTextField();
        add(new JScrollPane(equipmentTable), BorderLayout.CENTER);
    }

    public void refreshTable() {
        // TODO LEADER: fill equipmentTable with controller.listAllEquipment()
    }

    public void onSearchClick() {
        // TODO LEADER: filter table based on searchField text
    }
}
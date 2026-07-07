package view;

import controller.UserController;
import model.user.User;
import util.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserPanel extends JPanel {

    private final UserController controller = new UserController();
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, emailField, passwordField, additionalField;
    private JComboBox<String> userTypeCombo;

    public UserPanel() {
        initComponents();
        loadUserData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Table - shows all users
        String[] columns = {"User ID", "Name", "Email", "Role", "Card ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form Panel - for adding/editing users
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add/Edit User"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        nameField = new JTextField(15);
        formPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        emailField = new JTextField(15);
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        // User Type
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        userTypeCombo = new JComboBox<>(new String[]{"Student", "Staff"});
        formPanel.add(userTypeCombo, gbc);

        // Additional Info (Year for Student, Department for Staff)
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Year/Dept:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        additionalField = new JTextField(15);
        additionalField.setToolTipText("For Students: Year of Study (1-4), For Staff: Department");
        formPanel.add(additionalField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> loadUserData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Add button panel to form
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Table selection listener - populate fields for editing
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = userTable.getSelectedRow();
                if (row >= 0) {
                    nameField.setText((String) tableModel.getValueAt(row, 1));
                    emailField.setText((String) tableModel.getValueAt(row, 2));
                }
            }
        });

        add(formPanel, BorderLayout.SOUTH);
    }

    private void loadUserData() {
        tableModel.setRowCount(0); // Clear table
        List<User> users = controller.listAll();

        for (User user : users) {
            tableModel.addRow(new Object[]{
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getUserType().toString(),
                user.getCardId()
            });
        }
    }

    private void addUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(((JPasswordField) passwordField).getPassword());
        String userType = (String) userTypeCombo.getSelectedItem();
        String additional = additionalField.getText().trim();

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || additional.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields are required!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Validator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Email must be a valid email!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate Student year
        if (userType.equals("Student")) {
            try {
                int year = Integer.parseInt(additional);
                if (year < 1 || year > 4) {
                    JOptionPane.showMessageDialog(this,
                            "Year of Study must be between 1 and 4",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Year of Study must be a number",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String userId = controller.addUser(name, email, password, userType, additional);

        JOptionPane.showMessageDialog(this,
                "User added successfully! User ID: " + userId,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        clearFields();
        loadUserData();
    }

    private void editUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to edit.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String userId = (String) tableModel.getValueAt(row, 0);
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name and Email are required!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prevent editing admin account
        if (userId.equals("USR-001")) {
            JOptionPane.showMessageDialog(this,
                    "Cannot edit the admin account.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Find the user and update
        try {
            controller.updateUser(userId, newName, newEmail);
            JOptionPane.showMessageDialog(this,
                    "User updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadUserData();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to update user: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to delete.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String userId = (String) tableModel.getValueAt(row, 0);

        // Prevent deleting admin account
        if (userId.equals("USR-001")) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete the admin account.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user " + userId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            controller.removeUser(userId);
            JOptionPane.showMessageDialog(this,
                    "User deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadUserData();
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        additionalField.setText("");
        userTypeCombo.setSelectedIndex(0);
    }
}
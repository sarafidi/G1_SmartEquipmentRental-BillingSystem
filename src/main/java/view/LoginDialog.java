package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import controller.UserController;

public class LoginDialog extends JDialog {

    private final UserController userController = new UserController();
    private boolean loginSuccess = false;
    private JTextField userIdField;
    private JPasswordField passwordField;

    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Handle window close button (X)
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmExit();
            }
        });

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Smart Equipment Rental System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("User ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        userIdField = new JTextField(15);
        formPanel.add(userIdField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");
        // TODO: DELETE THIS BEFORE SUBMIT!!!!!!
        userIdField.setText("USR-0001");
        passwordField.setText("admin123");

        // Login button action
        loginButton.addActionListener(e -> {
            String userId = userIdField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (userId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both User ID and Password.",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            long start = System.currentTimeMillis();
            boolean success = userController.login(userId, password);
            System.out.println("[PERF] Login verification took: " + (System.currentTimeMillis() - start) + " ms");

            if (success) {
                loginSuccess = true;
                long disposeStart = System.currentTimeMillis();
                dispose(); // Close dialog
                System.out.println("[PERF] Dialog dispose took: " + (System.currentTimeMillis() - disposeStart) + " ms");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid User ID or Password. Please try again.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        });

        // Exit button action
        exitButton.addActionListener(e -> confirmExit());

        // Enable Enter key to trigger login
        getRootPane().setDefaultButton(loginButton);

        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit the application?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }
}
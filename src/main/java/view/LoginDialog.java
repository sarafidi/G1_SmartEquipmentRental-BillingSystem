package view;

import controller.UserController;

import javax.swing.*;

public class LoginDialog extends JDialog {
    private final UserController userController = new UserController();

    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);

        // TODO: TO BE IMPLEMENTED BY MEMBER C
        // Build login form UI here (userid field, password field, login button)
    }
}
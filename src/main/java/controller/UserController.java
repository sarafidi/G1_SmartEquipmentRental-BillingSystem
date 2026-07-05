package controller;

import model.RentalSystemFacade;
import model.user.User;
import util.HashUtil;
import util.SessionManager;

import java.util.List;

public class UserController {

    private final RentalSystemFacade facade = RentalSystemFacade.getInstance();

    public boolean login(String userId, String rawPassword) {
        // Find user by ID
        User user = facade.findById(userId);

        // If user not found, return false
        if (user == null) {
            return false;
        }

        // Verify password using HashUtil
        boolean isValid = HashUtil.verify(rawPassword, user.getPassword());

        // If valid, set current user in SessionManager
        if (isValid) {
            SessionManager.getInstance().setCurrentUser(user);
        }

        return isValid;
    }

    public void logout() {
        // Clear the session
        SessionManager.getInstance().clearSession();
    }

    public void addUser(User user) {
        // Add user using facade
        facade.addUser(user);
    }

    public void removeUser(String id) {
        // Don't allow deleting admin account
        if (id.equals("USR-001")) {
            return;
        }
        facade.removeUser(id);
    }

    public User findById(String id) {
        // Find user by ID using facade
        return facade.findById(id);
    }

    public List<User> listAll() {
        // Get all users using facade
        return facade.listAll();
    }

    public User getCurrentUser() {
        // Get currently logged in user
        return SessionManager.getInstance().getCurrentUser();
    }
}

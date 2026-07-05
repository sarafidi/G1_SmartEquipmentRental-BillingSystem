package controller;

import model.RentalSystemFacade;
import model.user.User;

import java.util.List;

public class UserController {

    private final RentalSystemFacade facade = RentalSystemFacade.getInstance();

    public boolean login(String userId, String rawPassword) {
        // Find user by ID
        User user = facade.login(userId, rawPassword);
        return user != null;
    }

    public void logout() {
        // Clear the session
        facade.logout();
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
        // Get currently logged-in user
        return facade.getCurrentUser();
    }
}